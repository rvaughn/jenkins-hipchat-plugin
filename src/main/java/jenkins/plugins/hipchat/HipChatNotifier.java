package jenkins.plugins.hipchat;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.logging.Logger;

@SuppressWarnings({"unchecked"})
public class HipChatNotifier extends Notifier {

    private static final Logger logger = Logger.getLogger(HipChatNotifier.class.getName());

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    public String getRoom() {
        return getDescriptor().getRoom();
    }

    public String getAuthToken() {
        return getDescriptor().getToken();
    }

    public String getBuildServerUrl() {
        return getDescriptor().getBuildServerUrl();
    }

    public String getSendAs() {
        return getDescriptor().getSendAs();
    }
    
    @DataBoundConstructor
    public HipChatNotifier() {
        super();
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    public HipChatService newHipChatService(final String room) {
        return new StandardHipChatService(getAuthToken(), room == null ? getRoom() : room, getSendAs() == null ? "Build Server" : getSendAs());
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return true;
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        private String token;
        private String room;
        private String buildServerUrl;
        private String sendAs;

        public DescriptorImpl() {
            super(HipChatNotifier.class);
            load();
        }

        public String getToken() {
            return token;
        }

        public String getRoom() {
            return room;
        }

        public String getBuildServerUrl() {
            return buildServerUrl;
        }

        public String getSendAs() {
            return sendAs;
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public HipChatNotifier newInstance(StaplerRequest sr) {
            if (token == null) token = sr.getParameter("hipChatToken");
            if (buildServerUrl == null) buildServerUrl = sr.getParameter("hipChatBuildServerUrl");
            if (room == null) room = sr.getParameter("hipChatRoom");
            if (sendAs == null) sendAs = sr.getParameter("hipChatSendAs");
            return new HipChatNotifier();
        }

        @Override
        public boolean configure(StaplerRequest sr, JSONObject formData) throws FormException {
            token = sr.getParameter("hipChatToken");
            room = sr.getParameter("hipChatRoom");
            buildServerUrl = sr.getParameter("hipChatBuildServerUrl");
            sendAs = sr.getParameter("hipChatSendAs");
            if (buildServerUrl != null && !buildServerUrl.endsWith("/")) {
                buildServerUrl = buildServerUrl + "/";
            }
            try {
                new HipChatNotifier();
            } catch (Exception e) {
                throw new FormException("Failed to initialize notifier - check your global notifier configuration settings", e, "");
            }
            save();
            return super.configure(sr, formData);
        }

        @Override
        public String getDisplayName() {
            return "HipChat Notifications";
        }
    }
}
