package mauricecantaert.domain

import com.fasterxml.jackson.annotation.JsonBackReference
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne

@Entity(name = "equipment_request")
class EquipmentRequest : BaseEntity() {

    @ManyToOne(cascade = [CascadeType.ALL])
    @JsonBackReference
    lateinit var client: User

    @Column(name = "type")
    lateinit var type: EquipmentType

    @Column(name = "purpose")
    lateinit var purpose: EquipmentPurpose

    @Column(name = "weight")
    var weight: Int = 0

    @Column(name = "weight_unit")
    lateinit var weightUnit: WeightUnit

    companion object : PanacheCompanion<EquipmentRequest> {
        fun history(user: User) =
            EquipmentRequest.list("client.userName", user.userName)

        fun byId(id: Long) = EquipmentRequest.findById(id)
    }
}