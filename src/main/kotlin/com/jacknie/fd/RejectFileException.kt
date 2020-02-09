package com.jacknie.fd

import com.jacknie.fd.FileVerification

class RejectFileException(val verification: FileVerification): RuntimeException() {

}