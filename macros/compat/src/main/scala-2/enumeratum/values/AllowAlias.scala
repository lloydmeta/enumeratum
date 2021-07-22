package enumeratum.values

/**
  * Marker trait. You can extend this to tell enumeratum that you
  * want to allow 'aliases', i.e. define multiple entries with the
  * same value.
  *
  * Note: if you define multiple entries with the same value, calling
  * `withValue` for that value will return one of them. Which one it
  * returns is undefined.
  */
trait AllowAlias
