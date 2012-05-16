package br.com.caelum.vraptor;

public abstract class Option<T> {

	public static final <T> Option<T> valueOf(T obj) {
		if (obj == null) {
			return none();
		} else {
			return some(obj);
		}
	}

	public static final <X> Option<X> none() {
		return new None<X>();
	}

	public static <X> Option<X> some(X obj) {
		return new Some<X>(obj);
	}

	public boolean isEmpty() {
		return !isDefined();
	}

	public T getOrNull() {
		return getOrElse(null);
	}

	public abstract boolean isDefined();

	public abstract T get();

	public abstract T getOrElse(T another);

	public abstract Option<T> orElse(Option<T> alternative);
}

class Some<T> extends Option<T> {
	private final T obj;

	Some(T obj) {
		this.obj = obj;
	}

	@Override
	public boolean isDefined() {
		return true;
	}

	@Override
	public T get() {
		return obj;
	}

	@Override
	public T getOrElse(T another) {
		return get();
	}

	@Override
	public Option<T> orElse(Option<T> alternative) {
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.obj == null) ? 0 : this.obj.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Some)) {
			return false;
		}
		Some<T> other = (Some<T>) obj;
		if (this.obj == null) {
			if (other.obj != null) {
				return false;
			}
		} else if (!this.obj.equals(other.obj)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Some(" + this.obj + ")";
	}
}

class None<T> extends Option<T> {

	@Override
	public boolean isDefined() {
		return false;
	}

	@Override
	public T get() {
		throw new IllegalStateException("No value defined");
	}

	@Override
	public T getOrElse(T another) {
		return another;
	}

	@Override
	public Option<T> orElse(Option<T> alternative) {
		return alternative;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof None) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public String toString() {
		return "None";
	}
}
