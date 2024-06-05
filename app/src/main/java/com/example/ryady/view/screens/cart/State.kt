package com.example.payment

sealed
class State {
    class Success(val data:PaymentCreationResult) : State()
    class Failure(val msg:Throwable) : State()
    object Loading : State()
}
