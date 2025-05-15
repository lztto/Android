package com.example.myadder;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ScrollView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView textExpression, textResult;
    StringBuilder expression = new StringBuilder();
    String currentInput = "";
    boolean isOperatorClicked = false;
    ArrayList<String> calculationHistory = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textExpression = findViewById(R.id.textExpression);
        textResult = findViewById(R.id.textResult);

        int[] numberBtnIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        };

        View.OnClickListener numberListener = v -> {
            Button btn = (Button) v;
            if (isOperatorClicked) {
                isOperatorClicked = false;
            }
            currentInput += btn.getText().toString();
            expression.append(btn.getText().toString());
            textExpression.setText(expression.toString());
            textResult.setText(currentInput);
        };

        for (int id : numberBtnIds) {
            findViewById(id).setOnClickListener(numberListener);
        }

        setOperatorListener(R.id.btnPlus, "+");
        setOperatorListener(R.id.btnMinus, "-");
        setOperatorListener(R.id.btnMultiply, "×");
        setOperatorListener(R.id.btnDivide, "÷");

        // AC
        findViewById(R.id.btnAC).setOnClickListener(v -> {
            currentInput = "";
            expression.setLength(0);
            textExpression.setText("");
            textResult.setText("0");
            isOperatorClicked = false;
        });

        // HOME (앱 종료)
        findViewById(R.id.btnHome).setOnClickListener(v -> finish());

        // BACKSPACE
        findViewById(R.id.btnBackspace).setOnClickListener(v -> {
            if (expression.length() > 0) {
                expression.setLength(expression.length() - 1);
                if (!currentInput.isEmpty()) {
                    currentInput = currentInput.substring(0, currentInput.length() - 1);
                }
                textExpression.setText(expression.toString());
                textResult.setText(currentInput.isEmpty() ? "0" : currentInput);
            }
        });

        // 괄호
        findViewById(R.id.btnOpenParen).setOnClickListener(v -> {
            expression.append("(");
            currentInput += "(";
            textExpression.setText(expression.toString());
            textResult.setText(currentInput);
        });

        findViewById(R.id.btnCloseParen).setOnClickListener(v -> {
            expression.append(")");
            currentInput += ")";
            textExpression.setText(expression.toString());
            textResult.setText(currentInput);
        });

        // 계산
        findViewById(R.id.btnEqual).setOnClickListener(v -> {
            if (expression.length() > 0) {
                try {
                    String expr = expression.toString()
                            .replace("×", "*")
                            .replace("÷", "/");
                    
                    double result = evaluateExpression(expr);
                    String formattedResult = formatNumber(result);
                    textResult.setText(formattedResult);
                    String fullExpression = expression.toString() + " = " + formattedResult;
                    textExpression.setText(fullExpression);
                    
                    // 계산 기록 저장
                    calculationHistory.add(fullExpression);
                    
                    currentInput = formattedResult;
                    expression.setLength(0);
                    expression.append(formattedResult);
                } catch (Exception e) {
                    textResult.setText("Error");
                }
            }
        });

        // 계산 기록 버튼
        findViewById(R.id.btnHistory).setOnClickListener(v -> {
            showHistoryDialog();
        });

        // +/- 버튼
        findViewById(R.id.btnSign).setOnClickListener(v -> {
            if (!currentInput.isEmpty()) {
                if (currentInput.startsWith("-")) {
                    currentInput = currentInput.substring(1);
                    if (expression.length() >= currentInput.length() + 1) {
                        expression.delete(expression.length() - currentInput.length() - 1, expression.length());
                        expression.append(currentInput);
                    }
                } else {
                    currentInput = "-" + currentInput;
                    if (expression.length() >= currentInput.length() - 1) {
                        expression.delete(expression.length() - currentInput.length() + 1, expression.length());
                        expression.append(currentInput);
                    }
                }
                textExpression.setText(expression.toString());
                textResult.setText(currentInput);
            }
        });

        // % 버튼
        findViewById(R.id.btnPercent).setOnClickListener(v -> {
            if (!currentInput.isEmpty()) {
                try {
                    double value = Double.parseDouble(currentInput) / 100.0;
                    String formattedValue = formatNumber(value);
                    currentInput = formattedValue;
                    if (expression.length() >= currentInput.length()) {
                        expression.delete(expression.length() - currentInput.length(), expression.length());
                        expression.append(currentInput);
                    }
                    textExpression.setText(expression.toString());
                    textResult.setText(currentInput);
                } catch (Exception e) {
                    textResult.setText("Error");
                }
            }
        });
    }

    private void setOperatorListener(int buttonId, String operatorSymbol) {
        findViewById(buttonId).setOnClickListener(v -> {
            if (!isOperatorClicked && expression.length() > 0) {
                char lastChar = expression.charAt(expression.length() - 1);
                if (Character.isDigit(lastChar) || lastChar == ')') {
                    expression.append(operatorSymbol);
                    textExpression.setText(expression.toString());
                    currentInput = "";
                    isOperatorClicked = true;
                }
            }
        });
    }

    private double evaluateExpression(String expr) {
        try {
            // 괄호 처리
            while (expr.contains("(")) {
                int openIndex = expr.lastIndexOf("(");
                int closeIndex = expr.indexOf(")", openIndex);
                if (closeIndex == -1) throw new Exception("Invalid parentheses");
                
                String subExpr = expr.substring(openIndex + 1, closeIndex);
                double subResult = evaluateExpression(subExpr);
                
                // 괄호 앞에 숫자가 있는 경우 곱셈으로 처리
                if (openIndex > 0 && (Character.isDigit(expr.charAt(openIndex - 1)) || expr.charAt(openIndex - 1) == '.')) {
                    expr = expr.substring(0, openIndex) + "*" + subResult + expr.substring(closeIndex + 1);
                } else {
                    expr = expr.substring(0, openIndex) + subResult + expr.substring(closeIndex + 1);
                }
            }

            // 곱셈과 나눗셈 먼저 처리
            while (expr.contains("*") || expr.contains("/")) {
                int mulIndex = expr.indexOf("*");
                int divIndex = expr.indexOf("/");
                
                int opIndex;
                boolean isMul;
                
                if (mulIndex == -1) opIndex = divIndex;
                else if (divIndex == -1) opIndex = mulIndex;
                else opIndex = Math.min(mulIndex, divIndex);
                
                isMul = opIndex == mulIndex;

                // 연산자 앞뒤의 숫자 찾기
                int leftIndex = opIndex - 1;
                while (leftIndex > 0 && (Character.isDigit(expr.charAt(leftIndex - 1)) || expr.charAt(leftIndex - 1) == '.' || expr.charAt(leftIndex - 1) == '-')) {
                    leftIndex--;
                }
                
                int rightIndex = opIndex + 1;
                while (rightIndex < expr.length() && (Character.isDigit(expr.charAt(rightIndex)) || expr.charAt(rightIndex) == '.' || (rightIndex == opIndex + 1 && expr.charAt(rightIndex) == '-'))) {
                    rightIndex++;
                }

                double left = Double.parseDouble(expr.substring(leftIndex, opIndex));
                double right = Double.parseDouble(expr.substring(opIndex + 1, rightIndex));
                double result;

                if (isMul) result = left * right;
                else {
                    if (right == 0) throw new Exception("Division by zero");
                    result = left / right;
                }

                expr = expr.substring(0, leftIndex) + result + expr.substring(rightIndex);
            }

            // 덧셈과 뺄셈 처리
            if (expr.startsWith("+")) expr = expr.substring(1);
            
            String[] numbers = expr.split("[+]");
            double result = 0;
            
            for (String num : numbers) {
                if (num.contains("-")) {
                    String[] subNums = num.split("-");
                    double subResult;
                    if (subNums[0].isEmpty()) {
                        subResult = -Double.parseDouble(subNums[1]);
                        for (int i = 2; i < subNums.length; i++) {
                            subResult -= Double.parseDouble(subNums[i]);
                        }
                    } else {
                        subResult = Double.parseDouble(subNums[0]);
                        for (int i = 1; i < subNums.length; i++) {
                            subResult -= Double.parseDouble(subNums[i]);
                        }
                    }
                    result += subResult;
                } else if (!num.trim().isEmpty()) {
                    result += Double.parseDouble(num);
                }
            }
            
            return result;
        } catch (Exception e) {
            return 0;
        }
    }

    private String formatNumber(double number) {
        if (number == (long) number) {
            return String.format("%d", (long) number);
        } else {
            return String.valueOf(number);
        }
    }

    private void showHistoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("계산 기록");

        // 계산 기록이 없는 경우
        if (calculationHistory.isEmpty()) {
            builder.setMessage("계산 기록이 없습니다.");
        } else {
            // 계산 기록을 역순으로 표시 (최신 기록이 위에 오도록)
            StringBuilder historyText = new StringBuilder();
            for (int i = calculationHistory.size() - 1; i >= 0; i--) {
                historyText.append(calculationHistory.get(i)).append("\n\n");
            }
            
            // 스크롤 가능한 TextView 생성
            TextView textView = new TextView(this);
            textView.setPadding(30, 30, 30, 30);
            textView.setText(historyText.toString());
            
            ScrollView scrollView = new ScrollView(this);
            scrollView.addView(textView);
            
            builder.setView(scrollView);
            
            // 기록 삭제 버튼 추가
            builder.setNeutralButton("기록 삭제", (dialog, which) -> {
                calculationHistory.clear();
                dialog.dismiss();
            });
        }

        builder.setPositiveButton("확인", (dialog, which) -> dialog.dismiss());
        
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
