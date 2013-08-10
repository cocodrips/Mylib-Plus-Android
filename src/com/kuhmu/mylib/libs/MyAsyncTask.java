package com.kuhmu.mylib.libs;

import android.os.AsyncTask;

public abstract class MyAsyncTask<Params, Progress, Result> extends
        AsyncTask<Params, Progress, Result> {
    private AsyncTaskCallback callback;
    private int tag;

    public MyAsyncTask(AsyncTaskCallback callback, int tag) {
        this.callback = callback;
        this.tag = tag;
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        this.callback.onTaskEnd(true, tag, result);
    }

    public interface AsyncTaskCallback {
        public void onTaskEnd(Boolean isSuccesed, int tag, Object object);
    }
}
