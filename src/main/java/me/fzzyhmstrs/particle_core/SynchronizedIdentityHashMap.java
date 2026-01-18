package me.fzzyhmstrs.particle_core;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SynchronizedIdentityHashMap<K, V> extends IdentityHashMap<K, V> {

	private final IdentityHashMap<K, V> m;     // Backing Map
	final Object      mutex;        // Object on which to synchronize

	public SynchronizedIdentityHashMap(IdentityHashMap<K, V> m) {
		this.m = Objects.requireNonNull(m);
		mutex = this;
	}

	public int size() {
		synchronized (mutex) {return m.size();}
	}
	public boolean isEmpty() {
		synchronized (mutex) {return m.isEmpty();}
	}
	public boolean containsKey(Object key) {
		synchronized (mutex) {return m.containsKey(key);}
	}
	public boolean containsValue(Object value) {
		synchronized (mutex) {return m.containsValue(value);}
	}
	public V get(Object key) {
		synchronized (mutex) {return m.get(key);}
	}
	public V put(K key, V value) {
		synchronized (mutex) {return m.put(key, value);}
	}
	public V remove(Object key) {
		synchronized (mutex) {return m.remove(key);}
	}
	public void putAll(@NotNull Map<? extends K, ? extends V> map) {
		synchronized (mutex) {m.putAll(map);}
	}
	public void clear() {
		synchronized (mutex) {m.clear();}
	}

	private transient Set<K> keySet;
	private transient Set<Map.Entry<K, V>> entrySet;
	private transient Collection<V> values;

	public @NotNull Set<K> keySet() {
		synchronized (mutex) {
			if (keySet == null)
				keySet = new SynchronizedSet<>(m.keySet(), mutex);
			return keySet;
		}
	}

	public @NotNull Set<Map.Entry<K, V>> entrySet() {
		synchronized (mutex) {
			if (entrySet == null)
				entrySet = new SynchronizedSet<>(m.entrySet(), mutex);
			return entrySet;
		}
	}

	public @NotNull Collection<V> values() {
		synchronized (mutex) {
			if (values==null)
				values = new SynchronizedCollection<>(m.values(), mutex);
			return values;
		}
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	public boolean equals(Object o) {
		if (this == o)
			return true;
		synchronized (mutex) {return m.equals(o);}
	}
	public int hashCode() {
		synchronized (mutex) {return m.hashCode();}
	}
	public String toString() {
		synchronized (mutex) {return m.toString();}
	}

	// Override default methods in Map
	@Override
	public V getOrDefault(Object k, V defaultValue) {
		synchronized (mutex) {return m.getOrDefault(k, defaultValue);}
	}
	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		synchronized (mutex) {m.forEach(action);}
	}
	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		synchronized (mutex) {m.replaceAll(function);}
	}
	@Override
	public V putIfAbsent(K key, V value) {
		synchronized (mutex) {return m.putIfAbsent(key, value);}
	}
	@Override
	public boolean remove(Object key, Object value) {
		synchronized (mutex) {return m.remove(key, value);}
	}
	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		synchronized (mutex) {return m.replace(key, oldValue, newValue);}
	}
	@Override
	public V replace(K key, V value) {
		synchronized (mutex) {return m.replace(key, value);}
	}
	@Override
	public V computeIfAbsent(K key,
							 @NotNull Function<? super K, ? extends V> mappingFunction) {
		synchronized (mutex) {return m.computeIfAbsent(key, mappingFunction);}
	}
	@Override
	public V computeIfPresent(K key,
							  @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		synchronized (mutex) {return m.computeIfPresent(key, remappingFunction);}
	}
	@Override
	public V compute(K key,
					 @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		synchronized (mutex) {return m.compute(key, remappingFunction);}
	}
	@Override
	public V merge(K key, @NotNull V value,
				   @NotNull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		synchronized (mutex) {return m.merge(key, value, remappingFunction);}
	}

	private static class SynchronizedSet<E>
			extends SynchronizedCollection<E>
			implements Set<E> {

		SynchronizedSet(Set<E> s, Object mutex) {
			super(s, mutex);
		}

		@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
		public boolean equals(Object o) {
			if (this == o)
				return true;
			synchronized (mutex) { return c.equals(o); }
		}
		public int hashCode() {
			synchronized (mutex) {return c.hashCode();}
		}
	}

	static class SynchronizedCollection<E> implements Collection<E>, Serializable {

		final Collection<E> c;  // Backing Collection
		final Object mutex;     // Object on which to synchronize

		SynchronizedCollection(Collection<E> c, Object mutex) {
			this.c = Objects.requireNonNull(c);
			this.mutex = Objects.requireNonNull(mutex);
		}

		public int size() {
			synchronized (mutex) {return c.size();}
		}
		public boolean isEmpty() {
			synchronized (mutex) {return c.isEmpty();}
		}
		public boolean contains(Object o) {
			synchronized (mutex) {return c.contains(o);}
		}
		public Object @NotNull [] toArray() {
			synchronized (mutex) {return c.toArray();}
		}
		public <T> T @NotNull [] toArray(T @NotNull [] a) {
			synchronized (mutex) {return c.toArray(a);}
		}
		public <T> T[] toArray(IntFunction<T[]> f) {
			synchronized (mutex) {return c.toArray(f);}
		}

		public @NotNull Iterator<E> iterator() {
			return c.iterator(); // Must be manually synched by user!
		}

		public boolean add(E e) {
			synchronized (mutex) {return c.add(e);}
		}
		public boolean remove(Object o) {
			synchronized (mutex) {return c.remove(o);}
		}

		public boolean containsAll(@NotNull Collection<?> coll) {
			synchronized (mutex) {return c.containsAll(coll);}
		}
		public boolean addAll(@NotNull Collection<? extends E> coll) {
			synchronized (mutex) {return c.addAll(coll);}
		}
		public boolean removeAll(@NotNull Collection<?> coll) {
			synchronized (mutex) {return c.removeAll(coll);}
		}
		public boolean retainAll(@NotNull Collection<?> coll) {
			synchronized (mutex) {return c.retainAll(coll);}
		}
		public void clear() {
			synchronized (mutex) {c.clear();}
		}
		public String toString() {
			synchronized (mutex) {return c.toString();}
		}
		// Override default methods in Collection
		@Override
		public void forEach(Consumer<? super E> consumer) {
			synchronized (mutex) {c.forEach(consumer);}
		}
		@Override
		public boolean removeIf(Predicate<? super E> filter) {
			synchronized (mutex) {return c.removeIf(filter);}
		}
		@Override
		public Spliterator<E> spliterator() {
			return c.spliterator(); // Must be manually synched by user!
		}
		@Override
		public Stream<E> stream() {
			return c.stream(); // Must be manually synched by user!
		}
		@Override
		public Stream<E> parallelStream() {
			return c.parallelStream(); // Must be manually synched by user!
		}
	}
}