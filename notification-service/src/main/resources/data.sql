INSERT INTO email_templates (id, name, subject, body, type, is_active)
VALUES
    ('booking_confirmation', 'Booking Confirmation', 'Booking Confirmation - Galaxy View Tower Hotel',
    '<!DOCTYPE html>
    <html>
    <head>
        <style>
            body { font-family: Arial, sans-serif; }
            .container { max-width: 600px; margin: 0 auto; padding: 20px; }
            .header { background-color: #f8f9fa; padding: 20px; text-align: center; }
            .content { padding: 20px; }
            .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="header">
                <h1>Booking Confirmation</h1>
            </div>
            <div class="content">
                <p>Dear ${userName},</p>
                <p>Thank you for choosing Galaxy View Tower Hotel. Your booking has been confirmed.</p>
                <p>Booking ID: ${bookingId}</p>
                <p>We look forward to welcoming you!</p>
            </div>
            <div class="footer">
                <p>This is an automated message, please do not reply.</p>
            </div>
        </div>
    </body>
    </html>', 'EMAIL', true),

    ('payment_confirmation', 'Payment Confirmation', 'Payment Confirmation - Galaxy View Tower Hotel',
    '<!DOCTYPE html>
    <html>
    <head>
        <style>
            body { font-family: Arial, sans-serif; }
            .container { max-width: 600px; margin: 0 auto; padding: 20px; }
            .header { background-color: #f8f9fa; padding: 20px; text-align: center; }
            .content { padding: 20px; }
            .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="header">
                <h1>Payment Confirmation</h1>
            </div>
            <div class="content">
                <p>Dear ${userName},</p>
                <p>We have received your payment for your booking at Galaxy View Tower Hotel.</p>
                <p>Payment ID: ${paymentId}</p>
                <p>Thank you for your business!</p>
            </div>
            <div class="footer">
                <p>This is an automated message, please do not reply.</p>
            </div>
        </div>
    </body>
    </html>', 'EMAIL', true),

    ('cancellation_notice', 'Booking Cancellation', 'Booking Cancellation - Galaxy View Tower Hotel',
    '<!DOCTYPE html>
    <html>
    <head>
        <style>
            body { font-family: Arial, sans-serif; }
            .container { max-width: 600px; margin: 0 auto; padding: 20px; }
            .header { background-color: #f8f9fa; padding: 20px; text-align: center; }
            .content { padding: 20px; }
            .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="header">
                <h1>Booking Cancellation</h1>
            </div>
            <div class="content">
                <p>Dear ${userName},</p>
                <p>Your booking at Galaxy View Tower Hotel has been cancelled.</p>
                <p>Booking ID: ${bookingId}</p>
                <p>We hope to welcome you again in the future!</p>
            </div>
            <div class="footer">
                <p>This is an automated message, please do not reply.</p>
            </div>
        </div>
    </body>
    </html>', 'EMAIL', true); 