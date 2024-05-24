package mauricecantaert.dto

import jakarta.validation.constraints.NotNull

data class CreateInputDto(
    @NotNull val requestId: Long,
    @NotNull val input: String,
)