package mauricecantaert.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.OneToOne

/**
 * A suggestion by the LLM for an [EquipmentRequest]
 */
@Entity(name = "suggestion")
class Suggestion: BaseEntity() {

    /**
     * The equipment to generate a suggestion for.
     */
    @OneToOne
    lateinit var equipmentRequest: EquipmentRequest

    /**
     * The optional trainer's input that was used to generate the suggestion.
     */
    @OneToOne
    var trainerInput: TrainerInput? = null

    /**
     *  The generated suggestion, optionally based off [TrainerInput].
     */
    @Column(name = "suggestion", columnDefinition = "LONGTEXT")
    lateinit var suggestion: String
}
