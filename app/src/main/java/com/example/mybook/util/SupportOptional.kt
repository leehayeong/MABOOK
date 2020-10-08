package com.example.mybook.util

sealed class SupportOptional<out T : Any>(private val _value: T?) {

    // 클래스가 널이 아닌 값을 가지고 있는지?
    val isEmpty: Boolean
        get() = null == _value

    // 값을 반혼하기 전에 널 체크, 널을 반환하려 한다면 예외 발생
    val value: T
        get() = checkNotNull(_value)
}

// 빈 데이터를 표시하기 위한 클래스
class Empty<out T : Any> : SupportOptional<T>(null)

// 널 값이 아닌 데이터를 표시하기 위한 클래스
class Some<out T : Any>(value: T) : SupportOptional<T>(value)

// SupportOptional 형태로 감싸는 유틸리티 함수
inline fun <reified T : Any> optionalOf(value: T?) = if (null != value) Some(value) else Empty<T>()

// Empty 클래스의 인스턴스를 간편하게 만들어주는 유틸리티 함수
inline fun <reified T : Any> emptyOptional() = Empty<T>()
