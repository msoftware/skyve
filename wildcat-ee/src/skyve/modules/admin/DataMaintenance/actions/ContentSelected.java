package modules.admin.DataMaintenance.actions;

import modules.admin.domain.DataMaintenance;

import org.skyve.EXT;
import org.skyve.metadata.controller.ServerSideAction;
import org.skyve.metadata.controller.ServerSideActionResult;
import org.skyve.util.Util;
import org.skyve.web.WebContext;
import org.skyve.wildcat.content.AttachmentContent;
import org.skyve.wildcat.content.ContentManager;

public class ContentSelected implements ServerSideAction<DataMaintenance> {
	private static final long serialVersionUID = 8136709192590507528L;

	@Override
	public ServerSideActionResult execute(DataMaintenance bean, WebContext webContext)
	throws Exception {
		bean.setRefreshContent(Boolean.FALSE);

		String selectedContentId = bean.getSelectedContentId();
		if (selectedContentId == null) {
			bean.setContentLink(null);
		}
		else {
			try (ContentManager cm = EXT.newContentManager()) {
				AttachmentContent ac = cm.get(selectedContentId);
				if (ac == null) { // bean content, not attachment content
					bean.setContentLink(null);
				}
				else {
					bean.setContentLink(String.format("<a href=\"%s/content?_n=%s&_doc=%s.%s&_b=%s" + 
														"\" target=\"_content\">%s (%s)</a>",
														Util.getWildcatContextUrl(),
														bean.getSelectedContentId(),
														ac.getBizModule(),
														ac.getBizDocument(),
														ac.getAttributeName(),
														ac.getFileName(),
														ac.getMimeType()));
				}
			}
		}
		
		return new ServerSideActionResult(bean);
	}
}
