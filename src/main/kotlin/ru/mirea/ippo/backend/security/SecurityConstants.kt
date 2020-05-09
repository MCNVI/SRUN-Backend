package ru.mirea.ippo.backend.security

object SecurityConstants {
    // Signing key for HS512 algorithm
    const val JWT_SECRET = "qwJG5HsZb1kLJw2esreXiy7bIK3vyO2Wtw5_19UEzegj4ycoJK9LecEnXQ-fqZazKX-2X_rBOioQepEJqVuc8w"
    // JWT token defaults
    const val TOKEN_HEADER = "Authorization"
    const val TOKEN_PREFIX = "Bearer "
    const val EXPIRATION_TIME = 864_000_000
}
