package enumeratum

import play.api.data.Mapping
import play.api.libs.json.Format
import play.api.mvc.{ QueryStringBindable, PathBindable }

/**
 * An Enum that has a lot of the Play-related implicits built-in so you can avoid
 * boilerplate.
 *
 * Note, the binders created here are case-sensitive.
 *
 * Things included are:
 *
 *   - implicit JSON format
 *   - implicit PathBindable (for binding from request path)
 *   - implicit QueryStringBindable (for binding from query strings)
 *   - formField for doing things like `Form("hello" -> MyEnum.formField)`
 *
 */
trait PlayEnum[A] extends Enum[A] {

  implicit val jsonFormat: Format[A] = Json.formats(this)

  implicit val pathBindable: PathBindable[A] = UrlBinders.pathBinder(this)

  implicit val queryBindable: QueryStringBindable[A] = UrlBinders.queryBinder(this)

  val formField: Mapping[A] = Forms.enum(this)

}