package mauricecantaert.dto

import mauricecantaert.domain.EquipmentRequest
import mauricecantaert.domain.WeightUnit
import java.util.*

data class EquipmentRequestDto(
    val id: Long,
    val type: String,
    val purpose: String,
    val weight: Int,
    val weightUnit: String,
)

fun EquipmentRequest.toDto() = EquipmentRequestDto(
    id = id!!,
    type = type.name.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
    purpose = purpose.name.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
    weight = if (weightUnit == WeightUnit.KG) weight else (weight * 0.453592).toInt(),
    weightUnit = WeightUnit.KG.name
)