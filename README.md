# 💰 Gestão Financeira

A simple, offline personal finance app for Android, built with Kotlin and Jetpack Compose.

## Why I built this

I'm an Information Systems student and a developer intern. Between classes, work, bus fares, groceries and the occasional delivery order, my money was disappearing every month and I never really knew where it went.

For a while I tried to keep track of everything in a notes app and later in a spreadsheet. It worked for about two weeks. Opening a spreadsheet on my phone just to write down "lunch, R$ 25" was annoying, so I stopped doing it, and at the end of the month I was back to guessing.

Then I tried some of the popular finance apps. Most of them wanted me to connect my bank account, were full of ads, or locked basic features like charts behind a subscription. I didn't need any of that. I needed three things:

1. Write down what I spent in a few seconds
2. See how much of my monthly income is already gone
3. See which category is eating most of my money

Since I'm learning Kotlin and Android development anyway, I decided to stop looking for the perfect app and build my own. This project is the result: something I actually use every day, and also a way to practice building a real app from scratch.

## Features

- **Monthly income**: set how much you earn per month
- **Full CRUD for transactions**: add, list, edit and delete expenses and extra income
- **Balance overview**: monthly balance calculated as income + extra income − expenses
- **Spending progress bar**: shows what percentage of your income you have already spent, turning red when you pass 100%
- **Expenses by category chart**: horizontal bar chart showing where your money goes
- **Offline and private**: all data is stored locally on the device. No account, no internet, no ads

## Screenshots

_Coming soon_

## Tech stack

- **Kotlin**
- **Jetpack Compose** with **Material 3** for the UI
- **Local file storage** using the app's internal storage, with no external database libraries

The chart is built only with Compose layout components, so the project has no third-party dependencies beyond the default Android Studio template.

## How it works

The project is intentionally small, with one responsibility per file:

| File | Responsibility |
|------|----------------|
| `MainActivity.kt` | Entry point. Creates the repository and shows the main screen |
| `Transacao.kt` | Data model for a transaction, plus currency formatting and colors |
| `Repositorio.kt` | Reads and writes transactions and monthly income to the device's internal storage |
| `TelaPrincipal.kt` | Main screen: summary card, transaction list, add/edit dialog and income dialog |
| `Grafico.kt` | Bar chart of expenses grouped by category |

### Data flow

1. When the app opens, `MainActivity` creates a `Repositorio` and passes it to `TelaPrincipal`.
2. `TelaPrincipal` loads the saved transactions and income into Compose state.
3. When the user adds, edits or deletes a transaction, the state list is updated and the repository saves it to disk.
4. Because the list is Compose state, the balance, progress bar, chart and list all update automatically.

### Storage format

Transactions are saved in `transacoes.txt`, one per line, with fields separated by `|`:

```
id|description|amount|category|type|date
```

The monthly income is saved separately in `renda.txt`.

## Running the project

1. Clone the repository
   ```bash
   git clone https://github.com/YOUR_USERNAME/GestaoFinanceira.git
   ```
2. Open the folder in **Android Studio**
3. Wait for Gradle to sync
4. Run it on an emulator or on a physical device with USB debugging enabled

## Roadmap

- [ ] Filter transactions by month
- [ ] Monthly history and comparison between months
- [ ] Pie chart for categories
- [ ] Export data to CSV
- [ ] Migrate storage to Room database
- [ ] Dark mode tweaks

## License

This project is open source and available under the MIT License.
