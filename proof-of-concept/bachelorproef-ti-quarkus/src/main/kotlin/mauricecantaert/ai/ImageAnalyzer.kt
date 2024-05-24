package mauricecantaert.ai

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ImageContent
import dev.langchain4j.data.message.TextContent
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.output.Response
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import mauricecantaert.domain.EquipmentPurpose
import mauricecantaert.domain.EquipmentRequest
import mauricecantaert.domain.EquipmentType
import mauricecantaert.domain.WeightUnit
import org.apache.tika.Tika
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.io.File
import java.nio.file.Files
import java.util.*

/**
 * Langchain4j implementation to analyze images for fitness equipment using computer vision.
 *
 * @property projectId the Gemini project ID, injected from the root .env file.
 * @property projectLocation the Gemini project ID, injected from the root .env file.
 */
@ApplicationScoped
class ImageAnalyzer {
    @ConfigProperty(name = "GEMINI_PROJECT_ID")
    private lateinit var projectId: String

    @ConfigProperty(name = "GEMINI_PROJECT_LOCATION")
    private lateinit var projectLocation: String

    /**
     * Creates a request to find the fitness equipment's details from the provided image.
     */
    fun getEquipmentDetails(file: File): EquipmentRequest {
        // Encode the file to send to the back-end large language model
        val encodedFile = Base64.getEncoder().encodeToString(file.readBytes())
        val mime = Files.probeContentType(file.toPath()) ?: Tika().detect(file)

        // Define the destination containing the large language model
        val model: ChatLanguageModel = VertexAiGeminiChatModel.builder()
            .project(projectId)
            .location(projectLocation)
            .modelName("gemini-1.5-pro-preview-0514")
            .build()

        // Define the request to parse
        val userMessage: UserMessage = UserMessage.from(
            ImageContent.from(encodedFile, mime),
            TextContent.from(
                """
                        A user wants to know what fitness equipment he's looking at along with some details. Respond in the following JSON format.
                        If any details are unknown (such as weight or the type), try to make an estimation.

                        Answer with a single raw JSON document, WITHOUT any markdown markup such as ````json or ``` surrounding it.
                        The JSON document must contain:
                        - the type of equipment, such as a DUMBBELL or BARBELL, in the `type` key as a string
                        - the purpose of equipment, such as STRENGTH or CARDIO, in the `purpose` key as a string
                        - the weight of the equipment, in the `weight` key as a number
                        - the weight measurement, either KG or LBS, in the `weight_unit` key as a string
            """.trimIndent()
            )
        )

        // Map the response to a usable domain object and return it
        val response: Response<AiMessage> = model.generate(userMessage)
        Log.info(response.content().text())
        return ObjectMapper().readValue(response.content().text(), EquipmentRequestDto::class.java).toDomainObject()
    }
}

/**
 * Result mapper for Gemini responses.
 */
private class EquipmentRequestDto @JsonCreator constructor() {
    val type: String = ""
    val purpose: String = ""
    val weight: Int = -1
    @JsonProperty("weight_unit")
    val weightUnit: String = ""
}

/**
 * Converts the result to a domain object.
 */
private fun EquipmentRequestDto.toDomainObject(): EquipmentRequest {
    val dto = this
    val result = EquipmentRequest()
    result.type = EquipmentType.valueOf(dto.type.uppercase())
    result.weight = dto.weight
    result.purpose = EquipmentPurpose.valueOf(dto.purpose.uppercase())
    result.weightUnit = WeightUnit.valueOf(dto.weightUnit.uppercase())
    return result
}