package com.jacknie.filestore

import com.jacknie.filestore.model.UploadPolicy
import com.jacknie.filestore.model.UploadSource
import java.lang.RuntimeException

class UploadPolicyException(val source: UploadSource, val policy: UploadPolicy): RuntimeException()