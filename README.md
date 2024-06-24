# Ryady

## Table of Contents

- [Ryady](#ryady)
  - [Table of Contents](#table-of-contents)
  - [Project Description](#project-description)
  - [Project Members](#project-members)
  - [Task Management](#task-management)
  - [Project Repository](#project-repository)
  - [Architectural Design Pattern, Tools, and Libraries](#architectural-design-pattern-tools-and-libraries)
    - [Architectural Design Pattern](#architectural-design-pattern)
    - [Tools](#tools)
    - [Libraries](#libraries)
  - [Installation and Setup](#installation-and-setup)
  - [Contributing](#contributing)
  - [License](#license)

## Project Description

The Android M-Commerce Mobile App is a specialized platform for selling sportswear, designed to provide users with a smooth and elegant shopping experience. The app offers a wide range of sportswear products, ensuring that users can easily find and purchase their preferred items. The app aims to deliver a seamless and enjoyable shopping journey from browsing to checkout.

## Project Members

| Name            | Role/Task                                                       |
| --------------- | --------------------------------------------------------------- |
| Ahmed Goneim    | Cupons(Discount code) /Shopping Cart / Payment / Ads / Settings |
| Ali El-Sayed    | Brands / Categories / Orders / Home Design                      |
| Mohamed Hussein | Product info / Search / Favorites / Auth                        |

## Task Management

Tasks are managed using [Trello](<[TRELLO_LINK](https://trello.com/invite/b/fBmAA3OU/ATTI3233abee2f21a933dbf0cd3edc05bedc4ECD16D2/ryady)>).

## Project Repository

The project's source code is available on GitHub: [Project Repository](<[GITHUB_LINK](https://github.com/Ali-El-Sayed/Ryady)>).

## Architectural Design Pattern, Tools, and Libraries

### Architectural Design Pattern

- MVVM (Model-View-ViewModel): The app uses the MVVM architecture to separate concerns, making the codebase more manageable and testable.

### Tools

- Android SDK : The primary IDE for Android development.
- Kotlin : Main development Language
- Shopify Storefront GraphQL API : backend to manage the store
- Firebase : Used for backend services such as authentication and real-time database.

### Libraries

- Coroutines
- Coil
- Data Store
- Retrofit
- Apollo
- Gson
- Lottie
- hamcrest
- Robolectric

## Installation and Setup

Provide detailed instructions on how to set up the project locally.

1. Clone the repository:
   ```bash
   git clone https://github.com/Ali-El-Sayed/Ryady.git
   ```
2. Open the project using Android Studio
3. Wait until loading dependencies
4. Run the Project

## Contributing

Guidelines for contributing to the project.

1. Fork the repository.
2. Create a new branch:
   ```bash
   git checkout -b feature-branch
   ```
3. Make your changes and commit them:
   ```bash
   git commit -m "Description of the changes"
   ```
4. Push to the branch:
   ```bash
   git push origin feature-branch
   ```
5. Create a pull request.

## License

```
MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
