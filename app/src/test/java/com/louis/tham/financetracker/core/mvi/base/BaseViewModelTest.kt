package com.louis.tham.financetracker.core.mvi.base

import app.cash.turbine.test
import com.louis.tham.financetracker.utils.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

data class TestState(val count: Int = 0) : UiState
sealed interface TestIntent : UiIntent {
    data class Increment(val amount: Int) : TestIntent
    object TriggerEffect : TestIntent
}
sealed interface TestEffect : UiEffect {
    data class ShowToast(val message: String) : TestEffect
}

class ConcreteViewModel : BaseViewModel<TestState, TestIntent, TestEffect>() {
    override fun createInitialState(): TestState = TestState(count = 0)

    override fun handleIntent(intent: TestIntent) {
        when (intent) {
            is TestIntent.Increment -> {
                setState { copy(count = count + intent.amount) }
            }
            is TestIntent.TriggerEffect -> {
                setEffect { TestEffect.ShowToast("Counter event triggered") }
            }
        }
    }
}

class BaseViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun initialState_isSetProperly() {
        val viewModel = ConcreteViewModel()
        assertEquals(TestState(count = 0), viewModel.uiState.value)
        assertEquals(TestState(count = 0), viewModel.currentState)
    }

    @Test
    fun sendIntent_updatesStateCorrectly() = runTest {
        val viewModel = ConcreteViewModel()

        viewModel.uiState.test {
            assertEquals(TestState(count = 0), awaitItem())

            viewModel.sendIntent(TestIntent.Increment(5))
            assertEquals(TestState(count = 5), awaitItem())

            viewModel.sendIntent(TestIntent.Increment(3))
            assertEquals(TestState(count = 8), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun setEffect_emitsSideEffectCorrectly() = runTest {
        val viewModel = ConcreteViewModel()

        viewModel.effect.test {
            viewModel.sendIntent(TestIntent.TriggerEffect)

            val effect = awaitItem()
            assertEquals(TestEffect.ShowToast("Counter event triggered"), effect)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
