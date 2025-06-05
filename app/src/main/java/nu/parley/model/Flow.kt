package nu.parley.model

sealed class Flow {
    /**
     * The default and recommended flow to use Parley.
     */
    data class Default(
        val openChatDirectly: Boolean,
    ) : Flow()

    /**
     * A custom flow which requires knowledge about the considerations and pitfalls of this method.
     */
    data object SpecialLightweight : Flow()
}