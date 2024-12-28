# Zero-Knowledge Proof Authentication

This project demonstrates the implementation of Zero-Knowledge Proof (ZKP) authentication using the Schnorr protocol. ZKPs enable secure authentication by proving knowledge of a secret without revealing it.

## Features

- **Privacy-Preserving Authentication**: Authenticate without sharing sensitive data.
- **Platform Compatibility**: Includes Kotlin-based mobile and server implementations, and a JavaScript frontend.
- **Schnorr Protocol**: Utilizes a secure, efficient ZKP protocol based on discrete logarithms.

## Overview

### Key Properties of Zero-Knowledge Proofs
1. **Completeness**: The verifier is convinced if the statement is true.
2. **Soundness**: False statements cannot convince the verifier.
3. **Zero Knowledge**: No additional information is revealed.

### Authentication Flow
1. **Register**: User credentials generate a verifier stored on the server.
2. **Challenge**: The server issues a random challenge.
3. **Proof**: The client generates a proof based on the challenge and private key.
4. **Verification**: The server validates the proof.

## Live Documentation
For a detailed explanation, visit the [documentation here](https://suwasto.github.io/kmpzkp/).

## Usage

### Registration Flow
1. The client sends the `username` and `verifier` to the server.
2. The server stores these securely.

### Challenge-Response Flow
1. The client requests a challenge by providing a username.
2. The server generates and sends a challenge.
3. The client computes a proof and sends it to the server.
4. The server verifies the proof to authenticate the user.

### Applications
- **Digital Signatures**: Secure and efficient signing.
- **Authentication**: Privacy-preserving identity verification.
- **Cryptocurrency**: Enhancing privacy and scalability.

## License
This project is licensed under the [Apache License 2.0](LICENSE).