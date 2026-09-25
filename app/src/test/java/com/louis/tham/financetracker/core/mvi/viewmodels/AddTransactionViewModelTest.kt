package com.louis.tham.financetracker.core.mvi.viewmodels

import app.cash.turbine.test
import com.louis.tham.financetracker.core.db.TransactionRepository
import com.louis.tham.financetracker.core.models.constants.TransactionType
import com.louis.tham.financetracker.core.mvi.contracts.AddTransactionEffect
import com.louis.tham.financetracker.core.mvi.contracts.AddTransactionIntent
import com.louis.tham.financetracker.core.mvi.contracts.TransactionField
import com.louis.tham.financetracker.fakes.FakeTransactionDao
import com.louis.tham.financetracker.utils.DateUtil
import com.louis.tham.financetracker.utils.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Calendar

class AddTransactionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeDao: FakeTransactionDao
    private lateinit var repository: TransactionRepository
    private lateinit var viewModel: AddTransactionViewModel

    @Before
    fun setUp() {
        fakeDao = FakeTransactionDao()
        repository = TransactionRepository(fakeDao)
        viewModel = AddTransactionViewModel(repository)
    }

    @Test
    fun initialState_hasExpectedDefaultValues() {
        val state = viewModel.currentState

        assertFalse(state.isLoading)
        assertEquals("", state.title)
        assertEquals("", state.amount)
        assertEquals("", state.category)
        assertEquals(TransactionType.EXPENSE, state.type)
        assertEquals(DateUtil.getCurrentDateDisplay(), state.date)
        assertEquals("", state.note)
        assertTrue(state.invalidFields.isEmpty())
        assertNull(state.errorMessage)
        assertFalse(state.showDatePicker)
    }

    @Test
    fun onTitleChanged_updatesTitleAndClearsTitleError() {
        // First simulate error on title
        viewModel.sendIntent(AddTransactionIntent.OnSaveClicked)
        assertTrue(viewModel.currentState.invalidFields.contains(TransactionField.TITLE))

        // Change title
        viewModel.sendIntent(AddTransactionIntent.OnTitleChanged("Groceries"))
        assertEquals("Groceries", viewModel.currentState.title)
        assertFalse(viewModel.currentState.invalidFields.contains(TransactionField.TITLE))
        assertNull(viewModel.currentState.errorMessage)
    }

    @Test
    fun onAmountChanged_withValidDigits_updatesAmountAndClearsError() {
        viewModel.sendIntent(AddTransactionIntent.OnAmountChanged("150"))

        assertEquals("150", viewModel.currentState.amount)
        assertFalse(viewModel.currentState.invalidFields.contains(TransactionField.AMOUNT))
        assertNull(viewModel.currentState.errorMessage)
    }

    @Test
    fun onAmountChanged_withInvalidCharacters_marksAmountAsInvalid() {
        viewModel.sendIntent(AddTransactionIntent.OnAmountChanged("abc"))

        assertTrue(viewModel.currentState.invalidFields.contains(TransactionField.AMOUNT))
    }

    @Test
    fun onCategoryChanged_updatesCategoryAndClearsCategoryError() {
        viewModel.sendIntent(AddTransactionIntent.OnSaveClicked)
        assertTrue(viewModel.currentState.invalidFields.contains(TransactionField.CATEGORY))

        viewModel.sendIntent(AddTransactionIntent.OnCategoryChanged("Other"))
        assertEquals("Other", viewModel.currentState.category)
        assertFalse(viewModel.currentState.invalidFields.contains(TransactionField.CATEGORY))
        assertNull(viewModel.currentState.errorMessage)
    }

    @Test
    fun onTypeSelected_updatesTransactionType() {
        viewModel.sendIntent(AddTransactionIntent.OnTypeSelected(TransactionType.INCOME))
        assertEquals(TransactionType.INCOME, viewModel.currentState.type)

        viewModel.sendIntent(AddTransactionIntent.OnTypeSelected(TransactionType.EXPENSE))
        assertEquals(TransactionType.EXPENSE, viewModel.currentState.type)
    }

    @Test
    fun onNoteChanged_updatesNote() {
        viewModel.sendIntent(AddTransactionIntent.OnNoteChanged("Dinner with friends"))
        assertEquals("Dinner with friends", viewModel.currentState.note)
    }

    @Test
    fun datePickerIntents_controlDatePickerStateProperly() {
        // Open date picker
        viewModel.sendIntent(AddTransactionIntent.OnDateTimeClicked)
        assertTrue(viewModel.currentState.showDatePicker)

        // Dismiss date picker
        viewModel.sendIntent(AddTransactionIntent.OnDismissDatePicker)
        assertFalse(viewModel.currentState.showDatePicker)

        // Open again and select date
        viewModel.sendIntent(AddTransactionIntent.OnDateTimeClicked)
        assertTrue(viewModel.currentState.showDatePicker)

        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.MAY, 10, 0, 0, 0)
        }
        val timestamp = calendar.timeInMillis
        viewModel.sendIntent(AddTransactionIntent.OnDateSelected(timestamp))

        assertFalse(viewModel.currentState.showDatePicker)
        assertEquals(DateUtil.convertMillisToString(timestamp), viewModel.currentState.date)
    }

    @Test
    fun onDismissError_clearsErrorMessage() {
        // Cause an error
        fakeDao.shouldThrowError = true
        fakeDao.errorToThrow = RuntimeException("Disk full")

        viewModel.sendIntent(AddTransactionIntent.OnTitleChanged("Snack"))
        viewModel.sendIntent(AddTransactionIntent.OnAmountChanged("5"))
        viewModel.sendIntent(AddTransactionIntent.OnCategoryChanged("Food"))
        viewModel.sendIntent(AddTransactionIntent.OnSaveClicked)

        assertEquals("Disk full", viewModel.currentState.errorMessage)

        // Dismiss error
        viewModel.sendIntent(AddTransactionIntent.OnDismissError)
        assertNull(viewModel.currentState.errorMessage)
    }

    @Test
    fun onSaveClicked_withEmptyFields_marksAllInvalidFields() = runTest {
        viewModel.sendIntent(AddTransactionIntent.OnSaveClicked)

        val errors = viewModel.currentState.invalidFields
        assertTrue(errors.contains(TransactionField.TITLE))
        assertTrue(errors.contains(TransactionField.AMOUNT))
        assertTrue(errors.contains(TransactionField.CATEGORY))

        // Ensure nothing was inserted
        fakeDao.getAllTransactions().test {
            val list = awaitItem()
            assertTrue(list.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onSaveClicked_withZeroAmount_marksAmountAsInvalid() {
        viewModel.sendIntent(AddTransactionIntent.OnTitleChanged("Bus"))
        viewModel.sendIntent(AddTransactionIntent.OnAmountChanged("0"))
        viewModel.sendIntent(AddTransactionIntent.OnCategoryChanged("Transport"))
        viewModel.sendIntent(AddTransactionIntent.OnSaveClicked)

        val errors = viewModel.currentState.invalidFields
        assertTrue(errors.contains(TransactionField.AMOUNT))
        assertFalse(errors.contains(TransactionField.TITLE))
        assertFalse(errors.contains(TransactionField.CATEGORY))
    }

    @Test
    fun onSaveClicked_withValidData_insertsTransactionAndEmitsNavigateBack() = runTest {
        viewModel.effect.test {
            viewModel.sendIntent(AddTransactionIntent.OnTitleChanged("Book"))
            viewModel.sendIntent(AddTransactionIntent.OnAmountChanged("25"))
            viewModel.sendIntent(AddTransactionIntent.OnCategoryChanged("Other"))
            viewModel.sendIntent(AddTransactionIntent.OnNoteChanged("Unit Test"))
            viewModel.sendIntent(AddTransactionIntent.OnTypeSelected(TransactionType.EXPENSE))

            viewModel.sendIntent(AddTransactionIntent.OnSaveClicked)

            val effect = awaitItem()
            assertEquals(AddTransactionEffect.NavigateBack, effect)

            // Verify transaction was saved in repository
            fakeDao.getAllTransactions().test {
                val list = awaitItem()
                assertEquals(1, list.size)
                assertEquals("Book", list[0].title)
                assertEquals(25.0, list[0].amount, 0.001)
                assertEquals("Other", list[0].category)
                assertEquals(TransactionType.EXPENSE.name, list[0].type)
                assertEquals("Unit Test", list[0].note)
                cancelAndIgnoreRemainingEvents()
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onSaveClicked_whenRepositoryFails_setsErrorMessageAndClearsLoading() = runTest {
        fakeDao.shouldThrowError = true
        fakeDao.errorToThrow = RuntimeException("Database connection timeout")

        viewModel.sendIntent(AddTransactionIntent.OnTitleChanged("Gift"))
        viewModel.sendIntent(AddTransactionIntent.OnAmountChanged("50"))
        viewModel.sendIntent(AddTransactionIntent.OnCategoryChanged("Other"))

        viewModel.sendIntent(AddTransactionIntent.OnSaveClicked)

        assertFalse(viewModel.currentState.isLoading)
        assertEquals("Database connection timeout", viewModel.currentState.errorMessage)
    }
}
