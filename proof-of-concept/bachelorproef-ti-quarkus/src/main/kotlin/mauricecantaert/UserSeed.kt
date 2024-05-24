package mauricecantaert

import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import jakarta.transaction.Transactional
import mauricecantaert.domain.*

@ApplicationScoped
class UserInitializer {

    @Transactional
    fun init(@Observes startupEvent: StartupEvent) {
        if (User.count() > 0) return
        val testUser = User().apply { userName = "Maurice Cantaert" }
        testUser.persist()

        val existingRequest = EquipmentRequest().apply {
            client = testUser
            type = EquipmentType.DUMBBELL
            purpose = EquipmentPurpose.STRENGTH
            weight = 16
            weightUnit = WeightUnit.LBS
        }
        existingRequest.persist()

        val existingInput = TrainerInput().apply {
            equipmentRequest = existingRequest
            input = "The client should focus more on upper body exercises."
        }
        existingInput.persist()
    }
}