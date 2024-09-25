// SPDX-License-Identifier: GPL-3.0-or-later

package com.sciencesakura.gjsonp

import spock.lang.Specification

class ParserSpec extends Specification {

  def "parse '.foo'"() {
    given:
    def expression = '.foo'

    when:
    def instructions = new Parser(expression).toList()

    then:
    assert instructions == [
        new Instruction.GetElement('foo'),
    ]
  }

  def "parse '.foo.bar'"() {
    given:
    def expression = '.foo.bar'

    when:
    def instructions = new Parser(expression).toList()

    then:
    assert instructions == [
        new Instruction.GetElement('foo'),
        new Instruction.GetElement('bar'),
    ]
  }

  def "parse '[1]'"() {
    given:
    def expression = '[1]'

    when:
    def instructions = new Parser(expression).toList()

    then:
    assert instructions == [
        new Instruction.GetElement(1),
    ]
  }

  def "parse '[foo]'"() {
    given:
    def expression = '[foo]'

    when:
    def instructions = new Parser(expression).toList()

    then:
    assert instructions == [
        new Instruction.GetElement('foo'),
    ]
  }

  def "parse '[1][foo]'"() {
    given:
    def expression = '[1][foo]'

    when:
    def instructions = new Parser(expression).toList()

    then:
    assert instructions == [
        new Instruction.GetElement(1),
        new Instruction.GetElement('foo'),
    ]
  }

  def "parse '.foo[1]'"() {
    given:
    def expression = '.foo[1]'

    when:
    def instructions = new Parser(expression).toList()

    then:
    assert instructions == [
        new Instruction.GetElement('foo'),
        new Instruction.GetElement(1),
    ]
  }

  def "parse '[1].foo'"() {
    given:
    def expression = '[1].foo'

    when:
    def instructions = new Parser(expression).toList()

    then:
    assert instructions == [
        new Instruction.GetElement(1),
        new Instruction.GetElement('foo'),
    ]
  }

  def "throw exception for invalid expression: 'foo'"() {
    given:
    def expression = 'foo'

    when:
    new Parser(expression).toList()

    then:
    thrown(InvalidExpressionException)
  }

  def "throw exception for invalid expression: '.[1]'"() {
    given:
    def expression = '.[1]'

    when:
    new Parser(expression).toList()

    then:
    thrown(InvalidExpressionException)
  }

  def "throw exception for invalid expression: '[.]'"() {
    given:
    def expression = '[.]'

    when:
    new Parser(expression).toList()

    then:
    thrown(InvalidExpressionException)
  }

  def "throw exception for invalid expression: '[foo.bar]'"() {
    given:
    def expression = '[foo.bar]'

    when:
    new Parser(expression).toList()

    then:
    thrown(InvalidExpressionException)
  }

  def "throw exception for invalid expression: '.'"() {
    given:
    def expression = '.'

    when:
    new Parser(expression).toList()

    then:
    thrown(InvalidExpressionException)
  }
}
