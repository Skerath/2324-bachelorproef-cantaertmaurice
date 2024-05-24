package mauricecantaert.domain

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import jakarta.persistence.*

@Entity(name = "trainer_input")
@NamedQueries(
    NamedQuery(
        name = "TrainerInput.latest",
        query = """
            FROM trainer_input
            WHERE equipmentRequest.client.userName = ?1 AND equipmentRequest.type = ?2
            ORDER BY equipmentRequest.createdAt DESC
            """
    )
)
class TrainerInput : BaseEntity() {

    @OneToOne
    lateinit var equipmentRequest: EquipmentRequest

    @Column(name = "input", columnDefinition = "LONGTEXT")
    lateinit var input: String

    companion object : PanacheCompanion<TrainerInput> {
        fun latest(user: User, type: EquipmentType) =
            TrainerInput.find("#TrainerInput.latest", user.userName, type).firstResult()
    }
}