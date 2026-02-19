package org.ivcode.ai.plugin.api.chat.utils

import java.lang.reflect.AccessibleObject
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.kotlinFunction


internal fun AccessibleObject.getAnnotation(cls: Class<*>): Annotation? {
    // Use the KClass -> java Class conversion for comparison
    return this.annotations.firstOrNull { it.annotationClass.java == cls }
}

internal fun <T: Annotation> Any.getAnnotation(annotationClass: Class<T>): T? {
    // Delegate to the Java Class#getAnnotation which returns the annotation instance (or null)
    return this::class.java.getAnnotation(annotationClass)
}

internal fun Any.getFields(): List<Field> {
    return this::class.java.declaredFields.toList()
}

internal fun Any.getMethods(): List<Method> {
    return this::class.java.declaredMethods.toList()
}

fun Parameter.isKotlinNullable(): Boolean {
    // declaringExecutable should always be present for a Parameter obtained from reflection;
    // throw an explicit exception if it isn't to fail fast and surface unexpected conditions.
    val exec = this.declaringExecutable ?: throw IllegalStateException("Parameter has no declaring executable")
    return when (exec) {
        is Method -> exec.isParameterNullable(this)
        is Constructor<*> -> exec.isParameterNullable(this)
    }
}

fun Parameter.isNotKotlinNullable() = !this.isKotlinNullable()

private fun Method.isParameterNullable(param: Parameter): Boolean {
    val kfun = this.kotlinFunction ?: return true
    return mapToKParameter(param, kfun).type.isMarkedNullable
}

private fun Constructor<*>.isParameterNullable(param: Parameter): Boolean {
    val kcons = this.kotlinFunction ?: return true
    return mapToKParameter(param, kcons).type.isMarkedNullable
}

private fun <F : kotlin.reflect.KFunction<*>> mapToKParameter(javaParam: Parameter, kfun: F): KParameter {
    // only consider value parameters (skip INSTANCE / EXTENSION_RECEIVER)
    val valueParams = kfun.parameters.filter { it.kind == KParameter.Kind.VALUE }
    val idx = javaParam.declaringExecutable.parameters.indexOf(javaParam)
    if(idx < 0 || idx >= valueParams.size) {
        throw IllegalArgumentException("Unable to map Java Parameter to Kotlin KParameter")
    }
    return valueParams[idx]
}