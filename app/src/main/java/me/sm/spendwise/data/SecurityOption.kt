data class SecurityOption(
    val id: Int,
    val name: String,
    val description: String
)

val securityOptions = listOf(
    SecurityOption(1, "Face ID", "Use face recognition to secure your app"),
    SecurityOption(2, "Fingerprint", "Use fingerprint authentication"),
    SecurityOption(3, "PIN", "Use a numeric PIN code"),
    SecurityOption(4, "Pattern", "Use pattern lock")
)
