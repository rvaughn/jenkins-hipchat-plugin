package jenkins.plugins.hipchat;

import hudson.Extension;
import hudson.model.*;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;

import java.util.Map;
import java.util.logging.Logger;

public class HipChatJobProperty extends hudson.model.JobProperty<AbstractProject<?, ?>> {

    private static final Logger logger = Logger.getLogger(HipChatNotifier.class.getName());

    private String room;
    private boolean startNotification;
    private boolean notifySuccess;
    private boolean notifyAborted;
    private boolean notifyNotBuilt;
    private boolean notifyUnstable;
    private boolean notifyFailure;

    @DataBoundConstructor
    public HipChatJobProperty(String room, boolean startNotification, boolean notifyAborted, boolean notifyFailure, boolean notifyNotBuilt, boolean notifySuccess, boolean notifyUnstable) {
        this.room = room;
        this.startNotification = startNotification;
        this.notifyAborted = notifyAborted;
        this.notifyFailure = notifyFailure;
        this.notifyNotBuilt = notifyNotBuilt;
        this.notifySuccess = notifySuccess;
        this.notifyUnstable = notifyUnstable;
    }

    @Exported
    public String getRoom() {
        return room;
    }

    @Exported
    public boolean getStartNotification() {
        return startNotification;
    }

    @Exported
    public boolean getNotifySuccess() {
        return notifySuccess;
    }
    
    @Override
    public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
        if (startNotification) {
            Map<Descriptor<Publisher>, Publisher> map = build.getProject().getPublishersList().toMap();
            for (Publisher publisher : map.values()) {
                if (publisher instanceof HipChatNotifier) {
                    logger.info("Invoking Started...");
                    new ActiveNotifier((HipChatNotifier) publisher).started(build);
                }
            }
        }
        return super.prebuild(build, listener);
    }

    @Exported
    public boolean getNotifyAborted() {
        return notifyAborted;
    }

    @Exported
    public boolean getNotifyFailure() {
        return notifyFailure;
    }

    @Exported
    public boolean getNotifyNotBuilt() {
        return notifyNotBuilt;
    }

    @Exported
    public boolean getNotifyUnstable() {
        return notifyUnstable;
    }

    @Extension
    public static final class DescriptorImpl extends JobPropertyDescriptor {
        public String getDisplayName() {
            return "HipChat Notifications";
        }

        @Override
        public boolean isApplicable(Class<? extends Job> jobType) {
            return true;
        }

        @Override
        public HipChatJobProperty newInstance(StaplerRequest sr, JSONObject formData) throws hudson.model.Descriptor.FormException {
            return new HipChatJobProperty(
                sr.getParameter("hipChatProjectRoom"),
                sr.getParameter("hipChatStartNotification") != null,
                sr.getParameter("hipChatNotifyAborted") != null,
                sr.getParameter("hipChatNotifyFailure") != null,
                sr.getParameter("hipChatNotifyNotBuilt") != null,
                sr.getParameter("hipChatNotifySuccess") != null,
                sr.getParameter("hipChatNotifyUnstable") != null);
        }
    }
}
