package nu.parley.android.util.mock

import nu.parley.android.data.model.Agent
import java.util.Random

internal object MockAgent {
    private fun generateRandomId() = Random().nextInt()

    private fun create(name: String): Agent {
        return Agent(name)
    }

    var Webuildapps: Agent = create("Webuildapps")
}
