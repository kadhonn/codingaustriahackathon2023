package at.nicerpricer.backend.service

import at.nicerpricer.backend.model.Data
import at.nicerpricer.backend.model.GroceryList
import at.nicerpricer.backend.model.ShoppingTrip
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
) {

    private val data: List<Data>
    private val indexSearcher: IndexSearcher
    private val analyzer: Analyzer

    init {
        data = loadData(filePath)

        val directory = ByteBuffersDirectory()
        analyzer = GermanAnalyzer()
        IndexWriter(directory, IndexWriterConfig(analyzer)).use {
            indexData(it, data)
        }

        val directoryReader = DirectoryReader.open(directory)
        indexSearcher = IndexSearcher(directoryReader)
    }

    fun first(): Data {
        return data.first()
    }

    fun query(names: String): List<String> {
        val query = buildQuery(names)
        val result = indexSearcher.search(query, 40)
        val storedFields = indexSearcher.storedFields()
        return result
            .scoreDocs
            .sortedBy { it.score }
            .reversed()
            .map { storedFields.document(it.doc).getField("name").stringValue() }
            .take(10)
    }

    private fun buildQuery(names: String): Query {
        val builder = BooleanQuery.Builder()
        analyzer.tokenStream("name", StringReader(names))
            .use { tokenStream ->
                val offsetAttribute = tokenStream.addAttribute(OffsetAttribute::class.java)
                tokenStream.reset()

                while (tokenStream.incrementToken()) {
                    builder.add(
                        BooleanClause(
                            WildcardQuery(
                                Term("name", "$offsetAttribute*")
                            ),
                            BooleanClause.Occur.MUST
                        )
                    )
                }
            }

        return builder.build()
    }

    fun shop(groceryList: GroceryList): ShoppingTrip {
        return ShoppingTripPlaner(groceryList, data).calculate()
    }

    private fun loadData(filePath: String): List<Data> {
        return JACKSON_MAPPER.readValue(
            File(filePath),
            object : TypeReference<List<Data>>() {})
    }

    private fun indexData(indexWriter: IndexWriter, data: List<Data>) {
        val distinctNames = data
            .map { it.name }
            .distinct()
        
        for (name in distinctNames) {
            val doc = Document()
            doc.add(TextField("name", name, Field.Store.YES))
            indexWriter.addDocument(doc)
        }
    }
}