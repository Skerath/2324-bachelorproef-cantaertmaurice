package mauricecantaert.ai

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.TextContent
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.output.Response
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel
import jakarta.enterprise.context.ApplicationScoped
import mauricecantaert.domain.EquipmentRequest
import mauricecantaert.domain.Suggestion
import mauricecantaert.domain.TrainerInput
import org.eclipse.microprofile.config.inject.ConfigProperty

/**
 * Langchain4j implementation to generate suggestions using generative AI.
 *
 * @property projectId the Gemini project ID, injected from the root .env file.
 * @property projectLocation the Gemini project ID, injected from the root .env file.
 */
@ApplicationScoped
class SuggestionGenerator {
    @ConfigProperty(name = "GEMINI_PROJECT_ID")
    private lateinit var projectId: String

    @ConfigProperty(name = "GEMINI_PROJECT_LOCATION")
    private lateinit var projectLocation: String

    /**
     * Creates a request to fetch suggestions, providing the trainer's input if present
     *
     * @return the newly generated [Suggestion]
     */
    fun getSuggestion(request: EquipmentRequest, trainerInput: TrainerInput?): Suggestion {
        val model: ChatLanguageModel = VertexAiGeminiChatModel.builder()
            .project(projectId)
            .location(projectLocation)
            .modelName("gemini-1.5-pro-preview-0514")
            .build()

        val userMessage: UserMessage = UserMessage.from(
            TextContent.from(
                """
                You're the assistant of a personal trainer helping their clients.
                A client has sent an image telling the trainer he has access to a ${request.type.name} of ${request.weight} ${request.weightUnit} and wishes suggestions regarding activities to use it in.
                ${
                    if (trainerInput == null) "" else """
                    The personal trainer has the following remarks for you to base your suggestions off:
                    ${trainerInput.input}
                """.trimIndent()
                }
                Provide an answer as if you were talking to the client directly, on behalve of the personal trainer.
                Keep in mind that the user will not be able to respond to your messages, so do not ask any questions.
                Instead, ask to upload a new image if the user wants more information.
            """.trimIndent()
            )
        )

        val response: Response<AiMessage> = model.generate(userMessage)
        return Suggestion().apply {
            this.suggestion = response.content().text()
            this.equipmentRequest = request
        }
    }
}