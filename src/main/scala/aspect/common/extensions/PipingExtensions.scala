package aspect.common.extensions

object PipingExtensions extends PipingExtensions

trait PipingExtensions {
  implicit class PipedObject[T](value: T) {
    def ~>[R](f: T => R): R = f(this.value)
    def pipe[R](f: T => R): R = f(this.value)
  }

  implicit class PipedFunc[T, R](f: T => R) {
    def <~[Z](v: Z => T): Z => R = x => f(v(x))
    def <~(v: T): R = f(v)
  }
}
