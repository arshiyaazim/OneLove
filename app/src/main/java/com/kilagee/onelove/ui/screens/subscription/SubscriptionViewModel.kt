package com.kilagee.onelove.ui.screens.subscription

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kilagee.onelove.data.model.Offer
import com.kilagee.onelove.data.model.PaymentMethod
import com.kilagee.onelove.data.model.PaymentType
import com.kilagee.onelove.data.model.Subscription
import com.kilagee.onelove.data.model.SubscriptionPlan
import com.kilagee.onelove.domain.repository.SubscriptionRepository
import com.kilagee.onelove.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for subscription screens
 */
@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    // UI state
    private val _uiState = MutableStateFlow<SubscriptionUiState>(SubscriptionUiState.Loading)
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()
    
    // One-time events
    private val _events = MutableSharedFlow<SubscriptionEvent>()
    val events: SharedFlow<SubscriptionEvent> = _events.asSharedFlow()
    
    // Subscription plans
    private val _subscriptionPlans = MutableStateFlow<List<SubscriptionPlan>>(emptyList())
    val subscriptionPlans: StateFlow<List<SubscriptionPlan>> = _subscriptionPlans.asStateFlow()
    
    // Current subscription
    private val _currentSubscription = MutableStateFlow<Subscription?>(null)
    val currentSubscription: StateFlow<Subscription?> = _currentSubscription.asStateFlow()
    
    // Selected plan
    private val _selectedPlan = MutableStateFlow<SubscriptionPlan?>(null)
    val selectedPlan: StateFlow<SubscriptionPlan?> = _selectedPlan.asStateFlow()
    
    // Selected billing interval
    private val _selectedBillingInterval = MutableStateFlow("monthly")
    val selectedBillingInterval: StateFlow<String> = _selectedBillingInterval.asStateFlow()
    
    // Payment methods
    private val _paymentMethods = MutableStateFlow<List<PaymentMethod>>(emptyList())
    val paymentMethods: StateFlow<List<PaymentMethod>> = _paymentMethods.asStateFlow()
    
    // Selected payment method
    private val _selectedPaymentMethod = MutableStateFlow<PaymentMethod?>(null)
    val selectedPaymentMethod: StateFlow<PaymentMethod?> = _selectedPaymentMethod.asStateFlow()
    
    // Promo code
    private val _promoCode = MutableStateFlow("")
    val promoCode: StateFlow<String> = _promoCode.asStateFlow()
    
    // Applied offer
    private val _appliedOffer = MutableStateFlow<Offer?>(null)
    val appliedOffer: StateFlow<Offer?> = _appliedOffer.asStateFlow()
    
    // Active offers
    private val _activeOffers = MutableStateFlow<List<Offer>>(emptyList())
    val activeOffers: StateFlow<List<Offer>> = _activeOffers.asStateFlow()
    
    // Checkout state
    private val _checkoutState = MutableStateFlow<CheckoutState>(CheckoutState.SelectPlan)
    val checkoutState: StateFlow<CheckoutState> = _checkoutState.asStateFlow()
    
    // Payment processing state
    private val _isProcessingPayment = MutableStateFlow(false)
    val isProcessingPayment: StateFlow<Boolean> = _isProcessingPayment.asStateFlow()
    
    // Payment client secret (for Stripe)
    private val _clientSecret = MutableStateFlow<String?>(null)
    val clientSecret: StateFlow<String?> = _clientSecret.asStateFlow()
    
    // Active jobs
    private var subscriptionJob: Job? = null
    
    init {
        loadSubscriptionPlans()
        loadCurrentSubscription()
        loadPaymentMethods()
        loadActiveOffers()
    }
    
    /**
     * Load subscription plans
     */
    private fun loadSubscriptionPlans() {
        viewModelScope.launch {
            _uiState.value = SubscriptionUiState.Loading
            
            val result = subscriptionRepository.getSubscriptionPlans()
            
            when (result) {
                is Result.Success -> {
                    _subscriptionPlans.value = result.data
                    
                    // Auto-select the most popular plan if available
                    result.data.find { it.isPopular }?.let {
                        _selectedPlan.value = it
                    } ?: run {
                        // Or select the first plan if none are marked as popular
                        if (result.data.isNotEmpty()) {
                            _selectedPlan.value = result.data[0]
                        }
                    }
                    
                    _uiState.value = SubscriptionUiState.Success
                }
                is Result.Error -> {
                    _events.emit(SubscriptionEvent.Error(result.message ?: "Failed to load subscription plans"))
                    _uiState.value = SubscriptionUiState.Error(result.message ?: "Failed to load subscription plans")
                }
                is Result.Loading -> {
                    // Keep loading state
                }
            }
        }
    }
    
    /**
     * Load current subscription
     */
    private fun loadCurrentSubscription() {
        // Cancel any existing job
        subscriptionJob?.cancel()
        
        // Start new job
        subscriptionJob = viewModelScope.launch {
            subscriptionRepository.getCurrentSubscriptionFlow().collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        _currentSubscription.value = result.data
                    }
                    is Result.Error -> {
                        _events.emit(SubscriptionEvent.Error(result.message ?: "Failed to load current subscription"))
                    }
                    is Result.Loading -> {
                        // Do nothing for loading state
                    }
                }
            }
        }
    }
    
    /**
     * Load payment methods
     */
    private fun loadPaymentMethods() {
        viewModelScope.launch {
            val result = subscriptionRepository.getPaymentMethods()
            
            if (result is Result.Success) {
                _paymentMethods.value = result.data
                
                // Auto-select the default payment method
                result.data.find { it.isDefault }?.let {
                    _selectedPaymentMethod.value = it
                }
            }
        }
    }
    
    /**
     * Load active offers
     */
    private fun loadActiveOffers() {
        viewModelScope.launch {
            val result = subscriptionRepository.getActiveOffers()
            
            if (result is Result.Success) {
                _activeOffers.value = result.data
            }
        }
    }
    
    /**
     * Select a subscription plan
     */
    fun selectPlan(plan: SubscriptionPlan) {
        _selectedPlan.value = plan
        
        // Clear applied offer when plan changes
        if (_appliedOffer.value != null && _appliedOffer.value?.planId != plan.id) {
            _appliedOffer.value = null
            _promoCode.value = ""
        }
    }
    
    /**
     * Select a billing interval
     */
    fun selectBillingInterval(interval: String) {
        _selectedBillingInterval.value = interval
    }
    
    /**
     * Select a payment method
     */
    fun selectPaymentMethod(paymentMethod: PaymentMethod) {
        _selectedPaymentMethod.value = paymentMethod
    }
    
    /**
     * Update promo code
     */
    fun updatePromoCode(code: String) {
        _promoCode.value = code
        // Clear applied offer when promo code changes
        _appliedOffer.value = null
    }
    
    /**
     * Apply promo code
     */
    fun applyPromoCode() {
        val code = _promoCode.value.trim()
        if (code.isEmpty()) return
        
        viewModelScope.launch {
            _uiState.value = SubscriptionUiState.Loading
            
            val result = subscriptionRepository.applyPromoCode(
                promoCode = code,
                planId = _selectedPlan.value?.id
            )
            
            when (result) {
                is Result.Success -> {
                    _appliedOffer.value = result.data
                    _events.emit(SubscriptionEvent.PromoCodeApplied(result.data))
                    _uiState.value = SubscriptionUiState.Success
                }
                is Result.Error -> {
                    _events.emit(SubscriptionEvent.Error(result.message ?: "Invalid promo code"))
                    _appliedOffer.value = null
                    _uiState.value = SubscriptionUiState.Success
                }
                is Result.Loading -> {
                    // Keep loading state
                }
            }
        }
    }
    
    /**
     * Clear applied promo code
     */
    fun clearPromoCode() {
        _promoCode.value = ""
        _appliedOffer.value = null
    }
    
    /**
     * Go to next checkout step
     */
    fun nextCheckoutStep() {
        _checkoutState.value = when (_checkoutState.value) {
            is CheckoutState.SelectPlan -> CheckoutState.SelectPayment
            is CheckoutState.SelectPayment -> CheckoutState.ReviewOrder
            else -> _checkoutState.value
        }
    }
    
    /**
     * Go to previous checkout step
     */
    fun previousCheckoutStep() {
        _checkoutState.value = when (_checkoutState.value) {
            is CheckoutState.SelectPayment -> CheckoutState.SelectPlan
            is CheckoutState.ReviewOrder -> CheckoutState.SelectPayment
            else -> _checkoutState.value
        }
    }
    
    /**
     * Add a new payment method
     */
    fun addPaymentMethod(paymentToken: String, type: PaymentType) {
        viewModelScope.launch {
            _uiState.value = SubscriptionUiState.Loading
            
            val result = subscriptionRepository.addPaymentMethod(
                paymentToken = paymentToken,
                type = type,
                isDefault = _paymentMethods.value.isEmpty() // Set as default if it's the first payment method
            )
            
            when (result) {
                is Result.Success -> {
                    _paymentMethods.value = _paymentMethods.value + result.data
                    _selectedPaymentMethod.value = result.data
                    _events.emit(SubscriptionEvent.PaymentMethodAdded)
                    _uiState.value = SubscriptionUiState.Success
                }
                is Result.Error -> {
                    _events.emit(SubscriptionEvent.Error(result.message ?: "Failed to add payment method"))
                    _uiState.value = SubscriptionUiState.Success
                }
                is Result.Loading -> {
                    // Keep loading state
                }
            }
        }
    }
    
    /**
     * Process subscription purchase
     */
    fun purchase() {
        val selectedPlan = _selectedPlan.value ?: return
        val selectedPaymentMethod = _selectedPaymentMethod.value ?: return
        val billingInterval = _selectedBillingInterval.value
        val promoCode = if (_appliedOffer.value != null) _promoCode.value.trim() else null
        
        viewModelScope.launch {
            _isProcessingPayment.value = true
            
            // For plans requiring immediate payment, create a payment intent
            if (selectedPlan.tier != com.kilagee.onelove.data.model.PlanTier.FREE) {
                // Calculate amount based on plan and billing interval
                val amount = if (billingInterval == "yearly") {
                    selectedPlan.pricePerYear
                } else {
                    selectedPlan.pricePerMonth
                }
                
                // Apply discount if there's a valid promo code
                val finalAmount = if (_appliedOffer.value != null) {
                    amount * (1 - _appliedOffer.value!!.discountPercentage / 100.0)
                } else {
                    amount
                }
                
                // Create payment intent
                val paymentIntentResult = subscriptionRepository.createPaymentIntent(
                    amount = finalAmount,
                    description = "Subscription to ${selectedPlan.name} (${billingInterval})"
                )
                
                if (paymentIntentResult is Result.Error) {
                    _events.emit(SubscriptionEvent.Error(paymentIntentResult.message ?: "Failed to create payment"))
                    _isProcessingPayment.value = false
                    return@launch
                }
                
                // Store client secret for Stripe SDK
                if (paymentIntentResult is Result.Success) {
                    _clientSecret.value = paymentIntentResult.data
                }
                
                // Confirm payment (in a real app, this would be handled by the Stripe SDK)
                // For now, we'll assume the payment was successful and proceed
            }
            
            // Create subscription
            val subscribeResult = subscriptionRepository.subscribe(
                planId = selectedPlan.id,
                paymentMethodId = selectedPaymentMethod.id,
                billingInterval = billingInterval,
                promoCode = promoCode
            )
            
            _isProcessingPayment.value = false
            
            when (subscribeResult) {
                is Result.Success -> {
                    _currentSubscription.value = subscribeResult.data
                    _events.emit(SubscriptionEvent.SubscriptionPurchased(subscribeResult.data))
                    _checkoutState.value = CheckoutState.Complete
                }
                is Result.Error -> {
                    _events.emit(SubscriptionEvent.Error(subscribeResult.message ?: "Failed to create subscription"))
                }
                is Result.Loading -> {
                    // Do nothing for loading state
                }
            }
        }
    }
    
    /**
     * Cancel current subscription
     */
    fun cancelSubscription(reason: String? = null, cancelImmediately: Boolean = false) {
        val subscription = _currentSubscription.value ?: return
        
        viewModelScope.launch {
            _uiState.value = SubscriptionUiState.Loading
            
            val result = subscriptionRepository.cancelSubscription(
                subscriptionId = subscription.id,
                reason = reason,
                cancelImmediately = cancelImmediately
            )
            
            when (result) {
                is Result.Success -> {
                    // The flow will update the current subscription
                    _events.emit(SubscriptionEvent.SubscriptionCanceled)
                    _uiState.value = SubscriptionUiState.Success
                }
                is Result.Error -> {
                    _events.emit(SubscriptionEvent.Error(result.message ?: "Failed to cancel subscription"))
                    _uiState.value = SubscriptionUiState.Success
                }
                is Result.Loading -> {
                    // Keep loading state
                }
            }
        }
    }
    
    /**
     * Update auto-renewal setting
     */
    fun updateAutoRenewal(autoRenew: Boolean) {
        val subscription = _currentSubscription.value ?: return
        
        viewModelScope.launch {
            val result = subscriptionRepository.updateAutoRenewal(
                subscriptionId = subscription.id,
                autoRenew = autoRenew
            )
            
            if (result is Result.Error) {
                _events.emit(SubscriptionEvent.Error(result.message ?: "Failed to update auto-renewal"))
            } else if (result is Result.Success) {
                _events.emit(SubscriptionEvent.AutoRenewalUpdated(autoRenew))
            }
        }
    }
    
    /**
     * Change subscription plan
     */
    fun changePlan(newPlanId: String) {
        val subscription = _currentSubscription.value ?: return
        
        viewModelScope.launch {
            _uiState.value = SubscriptionUiState.Loading
            
            val result = subscriptionRepository.changePlan(
                subscriptionId = subscription.id,
                newPlanId = newPlanId
            )
            
            when (result) {
                is Result.Success -> {
                    _currentSubscription.value = result.data
                    _events.emit(SubscriptionEvent.PlanChanged(result.data))
                    _uiState.value = SubscriptionUiState.Success
                }
                is Result.Error -> {
                    _events.emit(SubscriptionEvent.Error(result.message ?: "Failed to change plan"))
                    _uiState.value = SubscriptionUiState.Success
                }
                is Result.Loading -> {
                    // Keep loading state
                }
            }
        }
    }
    
    /**
     * Clear errors
     */
    fun clearErrors() {
        if (_uiState.value is SubscriptionUiState.Error) {
            _uiState.value = SubscriptionUiState.Success
        }
    }
    
    /**
     * Calculate the current price based on selected plan, interval, and any discounts
     */
    fun calculatePrice(): Double {
        val plan = _selectedPlan.value ?: return 0.0
        val basePrice = if (_selectedBillingInterval.value == "yearly") {
            plan.pricePerYear
        } else {
            plan.pricePerMonth
        }
        
        return if (_appliedOffer.value != null) {
            basePrice * (1 - _appliedOffer.value!!.discountPercentage / 100.0)
        } else {
            basePrice
        }
    }
    
    /**
     * Calculate yearly savings percentage compared to monthly billing
     */
    fun calculateYearlySavings(): Int {
        val plan = _selectedPlan.value ?: return 0
        val monthlyTotal = plan.pricePerMonth * 12
        val yearlyCost = plan.pricePerYear
        
        return if (monthlyTotal > 0) {
            ((monthlyTotal - yearlyCost) / monthlyTotal * 100).toInt()
        } else {
            0
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        subscriptionJob?.cancel()
    }
}

/**
 * UI state for the subscription screen
 */
sealed class SubscriptionUiState {
    object Loading : SubscriptionUiState()
    object Success : SubscriptionUiState()
    data class Error(val message: String) : SubscriptionUiState()
}

/**
 * Events emitted by the subscription screen
 */
sealed class SubscriptionEvent {
    data class PromoCodeApplied(val offer: Offer) : SubscriptionEvent()
    object PaymentMethodAdded : SubscriptionEvent()
    data class SubscriptionPurchased(val subscription: Subscription) : SubscriptionEvent()
    object SubscriptionCanceled : SubscriptionEvent()
    data class AutoRenewalUpdated(val autoRenew: Boolean) : SubscriptionEvent()
    data class PlanChanged(val subscription: Subscription) : SubscriptionEvent()
    data class Error(val message: String) : SubscriptionEvent()
}

/**
 * Checkout states
 */
sealed class CheckoutState {
    object SelectPlan : CheckoutState()
    object SelectPayment : CheckoutState()
    object ReviewOrder : CheckoutState()
    object Complete : CheckoutState()
}