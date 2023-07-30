package at.nicerpricer.backend.service

import at.nicerpricer.backend.model.*
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.de.GermanAnalyzer
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.TextField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.search.*
import org.apache.lucene.store.ByteBuffersDirectory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.StringReader

val JACKSON_MAPPER: ObjectMapper = ObjectMapper()
    .registerModule(KotlinModule.Builder().build())
    .registerModule(JavaTimeModule())

@Service
class DataService(
    @Value("\${nicerpricer.file.path}") filePath: String,
    @Value("\${nicerpricer.labels.path}") labelPath: String,
    @Value("\${nicerpricer.google.api-key}") private val googleApiKey: String,
) {

    private val data: PreparedDataHolder
    private val categories: Map<String, List<String>>
    private val indexSearcher: IndexSearcher
    private val analyzer: Analyzer

    init {
        data = loadData(filePath)
        categories = loadCategories(labelPath)

        val directory = ByteBuffersDirectory()
        analyzer = GermanAnalyzer()
        IndexWriter(directory, IndexWriterConfig(analyzer)).use {
            indexData(it, data.rawData)
            indexCategories(it, categories)
        }

        val directoryReader = DirectoryReader.open(directory)
        indexSearcher = IndexSearcher(directoryReader)
    }

    fun query(names: String): List<QueryItem> {
        val query = buildQuery(names)
        val result = indexSearcher.search(query, 10)
//        val explain = indexSearcher.explain(
//            query, result
//                .scoreDocs
//                .sortedBy { it.score }.first().doc
//        )
//        println(explain.toString())
        val storedFields = indexSearcher.storedFields()
        return result
            .scoreDocs
            .sortedBy { it.score }
            .reversed()
            .map {
                QueryItem(
                    storedFields.document(it.doc).getField("name").stringValue(),
                    storedFields.document(it.doc).getField("isCategory").stringValue().equals("true")
                )
            }
    }

    private fun buildQuery(names: String): Query {
        val builder = BooleanQuery.Builder()
        analyzer.tokenStream("name", StringReader(names))
            .use { tokenStream ->
                val offsetAttribute = tokenStream.addAttribute(OffsetAttribute::class.java)
                tokenStream.reset()

                while (tokenStream.incrementToken()) {
                    builder.add(
                        BooleanQuery.Builder()
                            .add(
                                WildcardQuery(
                                    Term("name", "$offsetAttribute*")
                                ),
                                BooleanClause.Occur.SHOULD
                            ).add(
                                BoostQuery(
                                    TermQuery(
                                        Term("name", offsetAttribute.toString())
                                    ),
                                    2f
                                ),
                                BooleanClause.Occur.SHOULD
                            ).build(),
                        BooleanClause.Occur.MUST
                    )
                }
            }

        return builder.build()
    }

    fun shop(groceryList: GroceryList): List<ShoppingTrip> {
        return ShoppingTripPlaner(googleApiKey, groceryList, data, categories).calculate()
    }

    private fun loadData(filePath: String): PreparedDataHolder {
        val rawData = JACKSON_MAPPER.readValue(
            File(filePath),
            object : TypeReference<List<Data>>() {})

        return PreparedDataHolder(
            rawData,
            rawData.groupBy { it.name!!.lowercase() }.mapValues { it.value.sortedBy { it.price } })
    }

    private fun loadCategories(labelPath: String): Map<String, List<String>> {
        val rawLabels = JACKSON_MAPPER.readValue(
            File(labelPath),
            object : TypeReference<List<Label>>() {})

        return rawLabels.groupBy({ it.productLabel!! }, { it.productName!! })
    }

    private fun indexData(indexWriter: IndexWriter, data: List<Data>) {
        val distinctNames = data
            .map { it.name }
            .distinct()

        for (name in distinctNames) {
            val doc = Document()
            doc.add(TextField("name", name, Field.Store.YES))
            doc.add(TextField("isCategory", "false", Field.Store.YES))
            indexWriter.addDocument(doc)
        }
    }

    private fun indexCategories(indexWriter: IndexWriter, categories: Map<String, List<String>>) {
        for (name in categories.keys) {
            val doc = Document()
            doc.add(TextField("name", name, Field.Store.YES))
            doc.add(TextField("isCategory", "true", Field.Store.YES))
            indexWriter.addDocument(doc)
        }
    }
}