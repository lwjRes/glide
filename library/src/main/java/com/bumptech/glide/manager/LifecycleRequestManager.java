package com.bumptech.glide.manager;

import com.bumptech.glide.request.Request;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class LifecycleRequestManager implements RequestManager {
    // Most requests will be for views and will therefore be held strongly (and safely) by the view via the tag.
    // However, a user can always pass in a different type of target which may end up not being strongly referenced even
    // though the user still would like the request to finish. Weak references are therefore only really functional in
    // this context for view targets. Despite the side affects, WeakReferences are still essentially required. A user
    // can always make repeated requests into targets other than views, or use an activity manager in a fragment pager
    // where holding strong references would steadily leak bitmaps and/or views.
    private final Set<Request> requests = Collections.newSetFromMap(new WeakHashMap<Request, Boolean>());

    @Override
    public void addRequest(Request request) {
        requests.add(request);
    }

    @Override
    public void removeRequest(Request request) {
        requests.remove(request);
    }

    public void onStart() {
        for (Request request : requests) {
            if (!request.isComplete() && !request.isRunning()) {
                request.run();
            }
        }

    }

    public void onStop() {
        for (Request request : requests) {
            if (!request.isComplete() && !request.isFailed()) {
                request.clear();
            }
        }
    }

    public void onDestroy() {
        for (Request request : requests) {
            request.clear();
        }
    }
}
