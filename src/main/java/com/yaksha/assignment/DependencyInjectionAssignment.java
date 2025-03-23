package com.yaksha.assignment;

// Define an interface for the message service
interface MessageService {
	void sendMessage(String message);
}

// A concrete implementation of MessageService (Dependency Injection)
class EmailService implements MessageService {

	@Override
	public void sendMessage(String message) {
		System.out.println("Sending email with message: " + message);
	}
}

// Another concrete implementation of MessageService (Dependency Injection)
class SMSService implements MessageService {

	@Override
	public void sendMessage(String message) {
		System.out.println("Sending SMS with message: " + message);
	}
}

// The application class that uses dependency injection
class MyApplication {
	private MessageService messageService; // This will be injected

	// Constructor-based dependency injection
	public MyApplication(MessageService messageService) {
		this.messageService = messageService;
	}

	// Method to access the dependency's method
	public void processMessage(String message) {
		messageService.sendMessage(message);
	}
}

public class DependencyInjectionAssignment {
	public static void main(String[] args) {
		// Dependency Injection: Creating the service to be injected
		MessageService emailService = new EmailService();

		// Injecting the dependency via the constructor
		MyApplication app = new MyApplication(emailService);

		// Accessing the DI class method
		app.processMessage("Hello, Dependency Injection!"); // Should print email sending message

		// Now let's switch the dependency to SMSService
		MessageService smsService = new SMSService();
		MyApplication appSMS = new MyApplication(smsService);

		appSMS.processMessage("Hello, Dependency Injection via SMS!"); // Should print SMS sending message
	}
}
