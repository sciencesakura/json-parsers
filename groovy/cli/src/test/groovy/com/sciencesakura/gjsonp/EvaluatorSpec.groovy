// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp

import spock.lang.Specification

class EvaluatorSpec extends Specification {

  def 'get element from array'() {
    given:
    def json = ['foo', 'bar', 'baz']
    def instructions = [
        new Instruction.GetElement(1),
    ]

    when:
    def actual = new Evaluator(instructions.iterator()).eval(json)

    then:
    assert actual == 'bar'
  }

  def 'get element from nested array'() {
    given:
    def json = [['foo', 'bar'], ['baz', 'qux']]
    def instructions = [
        new Instruction.GetElement(1),
        new Instruction.GetElement(0),
    ]

    when:
    def actual = new Evaluator(instructions.iterator()).eval(json)

    then:
    assert actual == 'baz'
  }

  def 'get property from object'() {
    given:
    def json = [foo: 1, bar: 2, baz: 3]
    def instructions = [
        new Instruction.GetElement('bar'),
    ]

    when:
    def actual = new Evaluator(instructions.iterator()).eval(json)

    then:
    assert actual == 2
  }

  def 'get property from nested object'() {
    given:
    def json = [foo: [bar: 1], baz: [qux: 2]]
    def instructions = [
        new Instruction.GetElement('baz'),
        new Instruction.GetElement('qux'),
    ]

    when:
    def actual = new Evaluator(instructions.iterator()).eval(json)

    then:
    assert actual == 2
  }
}
