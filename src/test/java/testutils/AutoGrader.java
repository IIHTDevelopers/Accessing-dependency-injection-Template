package testutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

public class AutoGrader {

	// Test if the code demonstrates proper dependency injection with interfaces and
	// classes
	public boolean testDependencyInjection(String filePath) throws IOException {
		System.out.println("Starting testDependencyInjection with file: " + filePath);

		File participantFile = new File(filePath); // Path to participant's file
		if (!participantFile.exists()) {
			System.out.println("File does not exist at path: " + filePath);
			return false;
		}

		FileInputStream fileInputStream = new FileInputStream(participantFile);
		JavaParser javaParser = new JavaParser();
		CompilationUnit cu;
		try {
			cu = javaParser.parse(fileInputStream).getResult()
					.orElseThrow(() -> new IOException("Failed to parse the Java file"));
		} catch (IOException e) {
			System.out.println("Error parsing the file: " + e.getMessage());
			throw e;
		}

		System.out.println("Parsed the Java file successfully.");

		// Use AtomicBoolean to allow modifications inside lambda expressions
		AtomicBoolean interfaceFound = new AtomicBoolean(false);
		AtomicBoolean emailServiceFound = new AtomicBoolean(false);
		AtomicBoolean smsServiceFound = new AtomicBoolean(false);
		AtomicBoolean appClassFound = new AtomicBoolean(false);
		AtomicBoolean emailServiceImplementsInterface = new AtomicBoolean(false);
		AtomicBoolean smsServiceImplementsInterface = new AtomicBoolean(false);
		AtomicBoolean methodsExecutedInMain = new AtomicBoolean(false);

		// Check for interface, class implementation, and method access
		System.out.println("------ Interface and Class Implementation Check ------");
		for (TypeDeclaration<?> typeDecl : cu.findAll(TypeDeclaration.class)) {
			if (typeDecl instanceof ClassOrInterfaceDeclaration) {
				ClassOrInterfaceDeclaration classDecl = (ClassOrInterfaceDeclaration) typeDecl;

				// Check for the MessageService interface
				if (classDecl.getNameAsString().equals("MessageService")) {
					System.out.println("Interface 'MessageService' found.");
					interfaceFound.set(true);
				}

				// Check for the EmailService class
				if (classDecl.getNameAsString().equals("EmailService")) {
					System.out.println("Class 'EmailService' found.");
					emailServiceFound.set(true);
					// Check if EmailService implements MessageService
					if (classDecl.getImplementedTypes().stream()
							.anyMatch(impl -> impl.getNameAsString().equals("MessageService"))) {
						emailServiceImplementsInterface.set(true);
						System.out.println("'EmailService' implements 'MessageService'.");
					} else {
						System.out.println("Error: 'EmailService' does not implement 'MessageService'.");
					}
				}

				// Check for the SMSService class
				if (classDecl.getNameAsString().equals("SMSService")) {
					System.out.println("Class 'SMSService' found.");
					smsServiceFound.set(true);
					// Check if SMSService implements MessageService
					if (classDecl.getImplementedTypes().stream()
							.anyMatch(impl -> impl.getNameAsString().equals("MessageService"))) {
						smsServiceImplementsInterface.set(true);
						System.out.println("'SMSService' implements 'MessageService'.");
					} else {
						System.out.println("Error: 'SMSService' does not implement 'MessageService'.");
					}
				}

				// Check for MyApplication class
				if (classDecl.getNameAsString().equals("MyApplication")) {
					System.out.println("Class 'MyApplication' found.");
					appClassFound.set(true);
				}
			}
		}

		// Ensure the interface and classes are correctly implemented
		if (!interfaceFound.get()) {
			System.out.println("Error: Interface 'MessageService' not found.");
			return false;
		}

		if (!emailServiceFound.get() || !smsServiceFound.get()) {
			System.out.println("Error: Class 'EmailService' or 'SMSService' not found.");
			return false;
		}

		if (!appClassFound.get()) {
			System.out.println("Error: Class 'MyApplication' not found.");
			return false;
		}

		if (!emailServiceImplementsInterface.get()) {
			System.out.println("Error: 'EmailService' does not implement 'MessageService'.");
			return false;
		}

		if (!smsServiceImplementsInterface.get()) {
			System.out.println("Error: 'SMSService' does not implement 'MessageService'.");
			return false;
		}

		// Check if methods are executed in the main method
		System.out.println("------ Method Execution Check in Main ------");

		for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
			if (method.getNameAsString().equals("main")) {
				if (method.getBody().isPresent()) {
					method.getBody().get().findAll(com.github.javaparser.ast.expr.MethodCallExpr.class)
							.forEach(callExpr -> {
								// Check if processMessage() is executed
								if (callExpr.getNameAsString().equals("processMessage")) {
									methodsExecutedInMain.set(true);
									System.out.println("Method 'processMessage' is executed in the main method.");
								}
							});
				}
			}
		}

		// Fail the test if methods weren't executed
		if (!methodsExecutedInMain.get()) {
			System.out.println("Error: 'processMessage' method not executed in the main method.");
			return false;
		}

		// If all checks pass
		System.out.println(
				"Test passed: Dependency Injection with interface and class methods are correctly implemented and accessed.");
		return true;
	}
}
