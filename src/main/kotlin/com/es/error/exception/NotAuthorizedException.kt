package com.es.gestionViajesAPI.error.exception

class NotAuthorizedException(message: String) : RuntimeException("Not Authorized Exception (401). $message") {
}