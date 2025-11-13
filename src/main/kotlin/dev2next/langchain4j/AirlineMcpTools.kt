package dev2next.langchain4j

import io.quarkiverse.mcp.server.Tool
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import java.io.InputStream

/**
 * MCP Server tools for airline loyalty program information.
 * These tools read information from PDF files stored in resources
 * and make it available to the AI assistant via Model Context Protocol.
 */
@ApplicationScoped
class AirlineMcpTools {

    companion object {
        private const val DELTA_PDF = "/rag/How to Get Medallion Status _ Delta Air Lines.pdf"
        private const val UNITED_PDF = "/rag/How to Earn Premier Status _ United Airlines.pdf"
    }

    /**
     * Reads Delta SkyMiles Medallion qualification requirements from PDF file.
     * Returns comprehensive information about earning Medallion status tiers.
     */
    @Tool(description = "Fetches current Delta SkyMiles Medallion qualification requirements and status tier information")
    fun getDeltaMedallionQualification(): String {
        Log.info("Reading Delta Medallion qualification information from PDF")
        return try {
            val text = readPdfFile(DELTA_PDF)
            
            Log.info("Successfully read Delta information (${text.length} characters)")
            
            // Return structured information
            """
            Source: Delta Air Lines PDF (Medallion Program)
            
            Content:
            ${text.take(5000)} // Limit to 5000 chars to avoid token overflow
            
            Note: This information is from Delta's official documentation and represents current qualification requirements.
            """.trimIndent()
        } catch (e: Exception) {
            Log.error("Failed to read Delta qualification information", e)
            "Error: Unable to read Delta qualification information. ${e.message}"
        }
    }

    /**
     * Reads United MileagePlus Premier qualification requirements from PDF file.
     * Returns comprehensive information about earning Premier status tiers.
     */
    @Tool(description = "Fetches current United MileagePlus Premier qualification requirements and status tier information")
    fun getUnitedPremierQualification(): String {
        Log.info("Reading United Premier qualification information from PDF")
        return try {
            val text = readPdfFile(UNITED_PDF)
            
            Log.info("Successfully read United information (${text.length} characters)")
            
            // Return structured information
            """
            Source: United Airlines PDF (MileagePlus Premier Program)
            
            Content:
            ${text.take(5000)} // Limit to 5000 chars to avoid token overflow
            
            Note: This information is from United's official documentation and represents current qualification requirements.
            """.trimIndent()
        } catch (e: Exception) {
            Log.error("Failed to read United qualification information", e)
            "Error: Unable to read United qualification information. ${e.message}"
        }
    }

    /**
     * Compares qualification requirements between Delta and United programs.
     * Reads information from both airline PDF files and provides a comparative analysis.
     */
    @Tool(description = "Compares Delta SkyMiles Medallion and United MileagePlus Premier qualification requirements")
    fun compareAirlinePrograms(): String {
        Log.info("Comparing Delta and United loyalty programs")
        
        val deltaInfo = getDeltaMedallionQualification()
        val unitedInfo = getUnitedPremierQualification()
        
        return """
        COMPARISON OF AIRLINE LOYALTY PROGRAMS
        
        === DELTA SKYMILES MEDALLION ===
        $deltaInfo
        
        === UNITED MILEAGEPLUS PREMIER ===
        $unitedInfo
        
        Use this information to provide a detailed comparison based on the customer's needs.
        """.trimIndent()
    }

    /**
     * Helper method to read text content from a PDF file.
     */
    private fun readPdfFile(resourcePath: String): String {
        val inputStream: InputStream = javaClass.getResourceAsStream(resourcePath)
            ?: throw IllegalArgumentException("PDF file not found: $resourcePath")
        
        return inputStream.use { stream ->
            val pdfBytes = stream.readAllBytes()
            Loader.loadPDF(pdfBytes).use { document ->
                val stripper = PDFTextStripper()
                stripper.getText(document)
            }
        }
    }
}
