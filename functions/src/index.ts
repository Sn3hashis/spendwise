import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';
import * as nodemailer from 'nodemailer';

admin.initializeApp();

// Configure nodemailer with your email service (e.g., Gmail)
const transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: functions.config().email.user,
        pass: functions.config().email.pass
    }
});

export const sendOTPEmail = functions.https.onCall(async (data, context) => {
    const { email, otp } = data;
    
    const mailOptions = {
        from: functions.config().email.user,
        to: email,
        subject: 'SpendWise - Email Verification Code',
        html: `
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2>Email Verification</h2>
                <p>Your verification code is:</p>
                <h1 style="font-size: 32px; letter-spacing: 5px; color: #1a73e8; text-align: center; padding: 20px;">
                    ${otp}
                </h1>
                <p>This code will expire in 5 minutes.</p>
                <p>If you didn't request this code, please ignore this email.</p>
            </div>
        `
    };

    try {
        await transporter.sendMail(mailOptions);
        return { success: true };
    } catch (error) {
        console.error('Error sending email:', error);
        throw new functions.https.HttpsError('internal', 'Error sending email');
    }
}); 