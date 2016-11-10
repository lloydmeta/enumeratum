package enumeratum

import argonaut.Argonaut._
import argonaut.{ DecodeJson, EncodeJson }

/**
 * Created by alonsodomin on 15/10/2016.
 */
package object values {

  implicit val argonautByteEncoder: EncodeJson[Byte] = EncodeJson { byte =>
    jNumber(byte.toShort)
  }

  implicit val argonautByteDecoder: DecodeJson[Byte] = DecodeJson { cursor =>
    cursor.as[Short].map(_.toByte)
  }

}
