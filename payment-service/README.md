# Payment Service

The Payment Service is a microservice responsible for handling payment processing in the hotel booking system. It supports multiple payment gateways (Stripe and PayPal) and implements idempotency to prevent duplicate charges.

## Features

- Payment processing with multiple gateways (Stripe and PayPal)
- Idempotent payment requests to prevent duplicate charges
- Secure webhook handling with signature validation
- Payment status tracking and management
- Refund processing
- Integration with Booking Service

## API Endpoints

### Initiate Payment

```http
POST /api/v1/payments
Content-Type: application/json
Idempotency-Key: <unique-key>

{
    "bookingId": "123e4567-e89b-12d3-a456-426614174000",
    "userId": "123e4567-e89b-12d3-a456-426614174001",
    "amount": 299.99,
    "currency": "USD",
    "paymentGateway": "STRIPE",
    "paymentMethodDetailsTokenized": "tok_visa_123",
    "idempotencyKey": "payment_123456789"
}
```

### Process Payment Callback

```http
POST /api/v1/payments/{paymentId}/callback?status=SUCCESS
Content-Type: application/json
Stripe-Signature: <webhook-signature>

{
    "type": "charge.succeeded",
    "data": {
        "object": {
            "id": "ch_123",
            "amount": 29999,
            "currency": "usd"
        }
    }
}
```

### Refund Payment

```http
POST /api/v1/payments/{paymentId}/refund
```

### Get Payment by ID

```http
GET /api/v1/payments/{paymentId}
```

### Get Payment by Booking ID

```http
GET /api/v1/payments/booking/{bookingId}
```

## Environment Variables

```yaml
stripe:
  api:
    key: sk_test_... # Stripe API key
  webhook:
    secret: whsec_... # Stripe webhook secret

paypal:
  client:
    id: ... # PayPal client ID
    secret: ... # PayPal client secret
```

## Integration with Booking Service

The Payment Service integrates with the Booking Service through the following flow:

1. Booking Service creates a booking and calls Payment Service to initiate payment
2. Payment Service processes the payment through the selected gateway
3. Payment Service receives webhook callbacks from the payment gateway
4. Payment Service updates the payment status
5. Booking Service can query payment status using the booking ID

## Idempotency

The service implements idempotency to prevent duplicate charges:

1. Each payment request must include an `Idempotency-Key` header or field
2. The service checks if a payment with the same key exists
3. If found, returns the existing payment record
4. If not found, creates a new payment

## Webhook Security

The service implements webhook security:

1. Validates Stripe webhook signatures using the webhook secret
2. Rejects requests with invalid signatures
3. Processes only verified webhook events

## Testing

Run the tests using:

```bash
./gradlew test
```

The test suite includes:

- Unit tests for service layer
- Integration tests for controller layer
- Tests for idempotency
- Tests for webhook security
- Tests for payment flows
