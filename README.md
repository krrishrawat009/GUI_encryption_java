# GUI_encryption_java
SecureEncryptor is a Java-based desktop application that provides a secure way for defence personnel to encrypt and decrypt messages and files. The application uses AES encryption and allows users to log their encryption/decryption activities. 


SecureEncryptor
SecureEncryptor is a Java-based desktop application that provides a secure way for defence personnel to encrypt and decrypt messages and files. The application uses AES encryption and allows users to log their encryption/decryption activities. It also includes features like confidentiality marking, key resetting, and file upload/download capabilities.

SecureEncryptor/
├── src/
│   └── main/
│       └── java/
│           └── EncryptWebsite1.java
├── resources/
│   └── defence.jpg
├── logs/
└── README.md

Application Flow Diagram
graph TD
    
    A[Login Panel] -->
    
    B[Encryption Panel]
    B --> C[Encrypt Message]
    B --> D[Decrypt Message]
    B --> E[Upload File]
    B --> F[Decrypt File]
    B --> G[Reset Key]
    B --> H[View Logs]
    B --> I[Clear Input/Output]
Features

Message Encryption and Decryption: The application allows users to encrypt and decrypt messages using AES encryption.
File Encryption and Decryption: Users can encrypt and decrypt files of various types, such as images, PDFs, and documents.
Confidentiality Marking: Users can mark messages and files as "Top Confidential" to indicate their sensitivity.
Key Resetting: Users can reset and reshuffle the encryption key to ensure the security of their data.
Logging: The application logs all encryption and decryption activities, including the message, file, confidentiality level, and timestamp.
User Authentication: The application includes a simple user authentication system to ensure that only authorized personnel can access the application.

Getting Started

Clone the repository: git clone https://github.com/username/SecureEncryptor.git
Open the project using your preferred Java IDE.
Build and run the EncryptWebsite1 class.
Log in using the default username "user" and password "password".
Explore the application's features and functionality.

Contributing
Contributions to the SecureEncryptor project are welcome. If you find any issues or have suggestions for improvements, please feel free to create a new issue or submit a pull request.
