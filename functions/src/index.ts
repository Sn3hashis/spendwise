import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';
import * as nodemailer from 'nodemailer';

admin.initializeApp();

// Email template for OTP
const createEmailTemplate = (otp: string) => `
    <!DOCTYPE html>
    <html>
    <head>
        <style>
            .container {
                font-family: Arial, sans-serif;
                max-width: 600px;
                margin: 0 auto;
                padding: 20px;
                background-color: #f7f7f7;
            }
            .header {
                text-align: center;
                padding: 20px;
                background-color: #1a73e8;
                color: white;
                border-radius: 8px 8px 0 0;
            }
            .content {
                background-color: white;
                padding: 20px;
                border-radius: 0 0 8px 8px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }
            .otp-code {
                font-size: 32px;
                letter-spacing: 5px;
                color: #1a73e8;
                text-align: center;
                padding: 20px;
                background-color: #f0f7ff;
                border-radius: 4px;
                margin: 20px 0;
            }
            .footer {
                text-align: center;
                color: #666;
                font-size: 12px;
                margin-top: 20px;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="header">
                <h2>SpendWise Email Verification</h2>
            </div>
            <div class="content">
                <p>Hello,</p>
                <p>Your verification code is:</p>
                <div class="otp-code">${otp}</div>
                <p>This code will expire in 5 minutes.</p>
                <p>If you didn't request this code, please ignore this email.</p>
            </div>
            <div class="footer">
                <p>This is an automated email. Please do not reply.</p>
                <p>&copy; ${new Date().getFullYear()} SpendWise. All rights reserved.</p>
            </div>
        </div>
    </body>
    </html>
`;

// Configure nodemailer
const createTransporter = () => {
    return nodemailer.createTransport({
        service: 'gmail',
        auth: {
            user: functions.config().email.user,
            pass: functions.config().email.pass
        }
    });
};

export const sendOTPEmail = functions.https.onCall(async (data, context) => {
    // Validate input
    if (!data.email || !data.otp) {
        throw new functions.https.HttpsError(
            'invalid-argument',
            'Email and OTP are required'
        );
    }

    const { email, otp } = data;

    // Validate email format
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        throw new functions.https.HttpsError(
            'invalid-argument',
            'Invalid email format'
        );
    }

    // Validate OTP format
    if (!/^\d{6}$/.test(otp)) {
        throw new functions.https.HttpsError(
            'invalid-argument',
            'OTP must be 6 digits'
        );
    }

    const mailOptions = {
        from: `"SpendWise" <${functions.config().email.user}>`,
        to: email,
        subject: 'SpendWise - Email Verification Code',
        html: createEmailTemplate(otp)
    };

    try {
        const transporter = createTransporter();
        await transporter.sendMail(mailOptions);
        return { success: true };
    } catch (error) {
        console.error('Error sending email:', error);
        throw new functions.https.HttpsError(
            'internal',
            'Failed to send verification email'
        );
    }
}); 