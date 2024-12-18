import SwiftUI
import Shared
import zkpschnorrproofs

struct ContentView: View {
    @State private var username: String = ""
    @State private var password: String = ""
    @State private var isLoading: Bool = false
    @State private var loginLog: String = ""
    @State private var users: [String: String] = [:]

    var body: some View {
        NavigationView {
            VStack {
                HStack {
                    // First Column for TextFields
                    VStack(alignment: .leading, spacing: 8) {
                        TextField("Username", text: $username)
                            .textFieldStyle(RoundedBorderTextFieldStyle())

                        TextField("Password", text: $password)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                    }
                    .frame(maxWidth: .infinity)
                    .padding()

                    // Second Column for Buttons
                    VStack(spacing: 8) {
                        Button("Login") {
                            login()
                        }
                        .buttonStyle(.borderedProminent)
                        .frame(maxWidth: .infinity)

                        Button("Register") {
                            register()
                        }
                        .buttonStyle(.bordered)
                        .frame(maxWidth: .infinity)
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                }
                .padding()

                // Loading Indicator
                if isLoading {
                    Text("Registering...")
                        .fontWeight(.bold)
                        .padding()
                }

                // Login Log Display
                if !loginLog.isEmpty {
                    VStack(alignment: .leading) {
                        Text("Login Log")
                            .font(.headline)
        
                        ScrollView { // Only this part is scrollable
                            Text(loginLog)
                                .font(.footnote)
                                .padding()
                        }
                        .frame(maxHeight: 150) // Limit the scrollable area height
                        .border(Color.gray, width: 1)
                    }
                    .padding()
                }

                // Users List
                Text("USERS:")
                    .font(.headline)
                    .padding(.top, 8)
                UsersList(users: users)

                Spacer()
            }
            .navigationTitle("Login Screen")
        }
    }

    // MARK: - Functions

    private func login() {
        guard let verifier = users[username] else {
            loginLog = "User not found"
            return
        }

        // Simulate fetching challenge and verifying proof (mocking functionality here)
        loginLog = "Fetching challenge..."
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            // server generate challenge
            let challenge = SchnorrServer().generateChallenge(verifier: verifier)
            
            // derive keys
            let generateKeys = SchnorrMobileClient().deriveKeys(username: username, password: password)
            
            loginLog += "\n\(challenge)\nGenerating proof..."
            let proof = SchnorrMobileClient().generateProof(privateKey: generateKeys.privateKey, challenge: challenge)

            // mockk send proof to server (proof.first and proof.second)
            DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                // verifying proff in server
                let commitment = (proof.first as String?) ?? ""
                let response = (proof.second as String?) ?? ""
                let verifyProff = SchnorrServer().verifyProof(
                    commitment: commitment, publicKey: verifier, challenge: challenge, response: response
                )
                loginLog += "\nCommitment: \(commitment)\nResponse: \(response)\nVerifying proof..."
                DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                    if verifyProff {
                        loginLog += "\nAuthenticated \(username)"
                    } else {
                        loginLog += "\nPassword is incorrect"
                    }
                }
            }
        }
    }

    private func register() {
        isLoading = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            let generateKeys = SchnorrMobileClient().deriveKeys(username: username, password: password)
            users[username] = generateKeys.publicKey // Mock public key generation
            isLoading = false
        }
    }
}

struct UsersList: View {
    let users: [String: String]

    var body: some View {
        List {
            ForEach(users.keys.sorted(), id: \.self) { key in
                VStack(alignment: .leading) {
                    Text("Name: \(key)")
                    Text("Verifier: \(users[key] ?? "")")
                        .font(.footnote)
                        .foregroundColor(.gray)
                }
                .padding(.vertical, 4)
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
