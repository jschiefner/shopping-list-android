package com.jschiefner.shoppinglist.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class RuleRepository {
    private RuleDao ruleDao;
    private LiveData<List<Rule>> categoryRules;

    public RuleRepository(Application application, long categoryId) {
        ItemDatabase database = ItemDatabase.getInstance(application);
        ruleDao = database.ruleDao();
        categoryRules = ruleDao.getCategoryRules(categoryId);
    }

    public void insert(Rule rule) {
        new InsertRuleTask(ruleDao).execute(rule);
    }

    public LiveData<List<Rule>> getCategoryRules() {
        return categoryRules;
    }

    private static class InsertRuleTask extends AsyncTask<Rule, Void, Void> {
        private RuleDao ruleDao;

        private InsertRuleTask(RuleDao ruleDao) {
            this.ruleDao = ruleDao;
        }

        @Override
        protected Void doInBackground(Rule... rules) {
            ruleDao.insert(rules[0]);
            return null;
        }
    }
}
