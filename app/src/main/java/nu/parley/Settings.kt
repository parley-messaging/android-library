package nu.parley

import nu.parley.model.Flow

object Settings {

    val flow: Flow = Flow.Default(openChatDirectly = false) // Recommended
//    val flow: Flow = Flow.Default(openChatDirectly = true) // Chat shows loader while configuring
//    val flow: Flow = Flow.SpecialLightweight // Requires special handling
    const val offlineMessaging = false
}