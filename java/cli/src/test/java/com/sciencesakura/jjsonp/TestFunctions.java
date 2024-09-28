// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.jjsonp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;

final class TestFunctions {

  private TestFunctions() {
  }

  static <K, V> SequencedMap<K, V> sequencedMapOf(K k1, V v1) {
    var map = new LinkedHashMap<>(Map.of(k1, v1));
    return Collections.unmodifiableSequencedMap(map);
  }

  static <K, V> SequencedMap<K, V> sequencedMapOf(K k1, V v1, K k2, V v2) {
    var map = new LinkedHashMap<K, V>() {
      {
        put(k1, v1);
        put(k2, v2);
      }
    };
    return Collections.unmodifiableSequencedMap(map);
  }

  static <K, V> SequencedMap<K, V> sequencedMapOf(K k1, V v1, K k2, V v2, K k3, V v3) {
    var map = new LinkedHashMap<K, V>() {
      {
        put(k1, v1);
        put(k2, v2);
        put(k3, v3);
      }
    };
    return Collections.unmodifiableSequencedMap(map);
  }

  static <E> List<E> toList(Iterator<E> iterator) {
    var list = new ArrayList<E>();
    while (iterator.hasNext()) {
      list.add(iterator.next());
    }
    return Collections.unmodifiableList(list);
  }
}
