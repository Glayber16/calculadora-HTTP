const express = require("express");

const app = express();

app.use(express.urlencoded({ extended: true }));
app.use(express.json());

// Função para validar e converter valores
function getPostValue(req, key) {
    return req.body[key] !== undefined ? parseFloat(req.body[key]) : null;
}


app.post("/calc", (req, res) => {
    const oper1 = getPostValue(req, "oper1");
    const oper2 = getPostValue(req, "oper2");
    const operacao = req.body.operacao !== undefined ? parseInt(req.body.operacao) : null;

    let resultado = null;
    let erro = null;

    if (oper1 === null || oper2 === null || operacao === null) {
        erro = "Parâmetros inválidos. Envie 'oper1', 'oper2' e 'operacao' via POST.";
    } else {
        switch (operacao) {
            case 1:
                resultado = oper1 + oper2;
                break;
            case 2:
                resultado = oper1 - oper2;
                break;
            case 3:
                resultado = oper1 * oper2;
                break;
            case 4:
                if (oper2 === 0) {
                    erro = "Erro: divisão por zero.";
                } else {
                    resultado = oper1 / oper2;
                }
                break;
            default:
                erro = "Operação inválida. Use 1 (soma), 2 (subtração), 3 (multiplicação) ou 4 (divisão).";
        }
    }

    if (erro) {
        res.json({ erro });
    } else {
        res.json({ oper1, oper2, operacao, resultado });
    }
});


function Expressao(expr) {
    // Remove espaços
    expr = expr.replace(/\s+/g, "");

    // 1 — Resolve parênteses recursivamente
    while (expr.includes("(")) {
        const match = expr.match(/\([^()]+\)/);
        if (!match) break;

        const dentro = match[0].substring(1, match[0].length - 1);
        const valor = Expressao(dentro);
        expr = expr.replace(match[0], valor);
    }

    // 2 — Tokeniza números e operadores (+ - * /)
    const tokens = expr.match(/\d+\.?\d*|[+\-*/]/g);
    if (!tokens) throw "Expressão inválida";

    // 3 — Resolve multiplicação e divisão
    for (let i = 0; i < tokens.length; i++) {
        if (tokens[i] === "*" || tokens[i] === "/") {
            let a = parseFloat(tokens[i - 1]);
            let b = parseFloat(tokens[i + 1]);
            let r = tokens[i] === "*" ? a * b : a / b;

            tokens.splice(i - 1, 3, String(r));  
            i--;
        }
    }

    // 4 — Resolve soma e subtração
    for (let i = 0; i < tokens.length; i++) {
        if (tokens[i] === "+" || tokens[i] === "-") {
            let a = parseFloat(tokens[i - 1]);
            let b = parseFloat(tokens[i + 1]);
            let r = tokens[i] === "+" ? a + b : a - b;

            tokens.splice(i - 1, 3, String(r));
            i--;
        }
    }

    return parseFloat(tokens[0]);
}



app.post("/expr", (req, res) => {
    const expr = req.body.expressao;

    if (!expr)
        return res.json({ erro: "Envie 'expressao' via POST." });

    try {
        const resultado = Expressao(expr);
        res.json({ expressao: expr, resultado });
    } catch (e) {
        res.json({ erro: "Erro ao processar: " + e });
    }
});

// Iniciar servidor
app.listen(3000, () => {
    console.log("Servidor Node.js rodando na porta 3000...");
});
