package mauricecantaert

import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import mauricecantaert.domain.EquipmentRequest
import mauricecantaert.domain.TrainerInput
import mauricecantaert.domain.User
import mauricecantaert.dto.CreateInputDto
import mauricecantaert.dto.EquipmentRequestDto
import mauricecantaert.dto.toDto


@Path("/api/trainer")
@Produces(MediaType.APPLICATION_JSON)
class TrainerResource {

    @GET
    fun getHistory(): List<EquipmentRequestDto> {
        val user = User.byName("Maurice Cantaert") ?: throw IllegalArgumentException("User can't be null")
        val result = EquipmentRequest.history(user)
        return result.map { it.toDto() }
    }

    @POST
    @Transactional
    fun createInput(
        @NotNull @Valid body: CreateInputDto
    ) {
        val correspondingRequest = EquipmentRequest.byId(body.requestId)
            ?: throw BadRequestException("Request can't be null")

        val result = TrainerInput().apply {
            equipmentRequest = correspondingRequest
            input = body.input
        }
        result.persist()
    }
}

