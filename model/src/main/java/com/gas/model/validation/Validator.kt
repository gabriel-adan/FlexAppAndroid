package com.gas.model.validation

class Validator {
    companion object {
        fun isValid(validationType: ValidationTypes, pattern: String?, value: String?): Boolean {
            when (validationType) {
                ValidationTypes.EQUALS -> {
                    return pattern == value
                }
                ValidationTypes.DISTINCT -> {
                    return pattern != value
                }
                ValidationTypes.LESS_THAN -> {
                    return (pattern ?: "") > (value ?: "")
                }
                ValidationTypes.LESS_THAN_OR_EQUALS -> {
                    return (pattern ?: "") >= (value ?: "")
                }
                ValidationTypes.GREATER_THAN -> {
                    return (pattern ?: "") < (value ?: "")
                }
                ValidationTypes.GREATER_THAN_OR_EQUALS -> {
                    return (pattern ?: "") <= (value ?: "")
                }
                ValidationTypes.STRING_NULL_OR_EMPTY -> {
                    return !value.isNullOrBlank()
                }
                ValidationTypes.STRING_MIN_LENGTH -> {
                    return if (value.isNullOrBlank()) {
                        false
                    } else {
                        val regex = "[0-9]+".toRegex()
                        if (regex.matches((pattern ?: ""))) {
                            pattern!!.toInt() <= value.length
                        } else {
                            false
                        }
                    }
                }
                ValidationTypes.STRING_MAX_LENGTH -> {
                    return if (value.isNullOrBlank()) {
                        false
                    } else {
                        val regex = "[0-9]+".toRegex()
                        if (regex.matches((pattern ?: ""))) {
                            pattern!!.toInt() >= value.length
                        } else {
                            false
                        }
                    }
                }
                ValidationTypes.REGEX -> {
                    if (pattern.isNullOrBlank())
                        return false
                    val regex = pattern.toRegex()
                    return regex.matches(value ?: "")
                }
            }
        }
    }
}