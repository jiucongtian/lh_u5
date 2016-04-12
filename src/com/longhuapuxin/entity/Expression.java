package com.longhuapuxin.entity;

import java.util.List;

/**
 * Created by ZH on 2016/1/26.
 * Email zh@longhuapuxin.com
 */
public class Expression {

    List<ExpressionNode> expressions;

    public List<ExpressionNode> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<ExpressionNode> expressions) {
        this.expressions = expressions;
    }

    public class ExpressionNode {
        private String name;
        private String file;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }
    }
}
