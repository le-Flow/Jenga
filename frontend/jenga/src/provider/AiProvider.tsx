import { Accessor, createContext, createEffect, createResource, createSignal, JSXElement, Setter, useContext } from "solid-js";
import { ProjectContext } from "./ProjectProvider";
import { UserContext } from "./UserProvider";
import { AiResourceService } from "../api";
import { AuthContext } from "./AuthProvider";

type AiContextType = {
    sessionId: Accessor<string | undefined>;
    setSessionId: Setter<string | undefined>;

    messages: Accessor<string[] | undefined>;
    setMessages: Setter<string[] | undefined>;

    sendMessage: (message: string) => void;
};

export const AiContext = createContext<AiContextType>();

interface ProviderProps {
    children: JSXElement;
}

export const AiProvider = (props: ProviderProps) => {
    const aCtx = useContext(AuthContext);
    const uCtx = useContext(UserContext);
    const pCtx = useContext(ProjectContext);

    const [sessionId, setSessionId] = createSignal<string>();
    const [messages, setMessages] = createSignal<string[]>();

    const [message, setMessage] = createSignal<string>();

    createEffect(() => {
        if (!sessionId()) {
            setMessages(undefined);
        }
    })

    createEffect(() => {
        if(!aCtx?.isLoggedIn?.()) {
            setSessionId(undefined);
            setMessages(undefined);
        }
    })
    
    const [response] = createResource(message, async (q) => {
        if (!q) {
            return undefined;
        }

        return await AiResourceService.postApiAiChat({
            conversationId: sessionId(),
            currentUser: uCtx?.user()?.username ?? "unknown",
            currentProjectID: pCtx?.selectedProject()?.identifier ?? "",
            currentTicketID: pCtx?.selectedTicket()?.id ?? 0,
            message: q
        });
    });

    createEffect(() => {
        const currentMessage = message();
        if (!currentMessage) {
            return;
        }

        setMessages(prev => [...(prev ?? []), currentMessage]);
    })

    createEffect(() => {
        if(!response.error) {
            const res = response();
            if(res) {
                setSessionId(res.conversationId);
                setMessages(prev => [...(prev ?? []), res.response]);
                setMessage(undefined);
            }
        }
    })

    createEffect(() => {
        console.log("Messages updated:", messages());
    })

    const sendMessage = (message: string) => {
        const trimmed = message.trim();
        if (!trimmed) return;

        setMessage(trimmed)
    };

    const value: AiContextType = {
        sessionId,
        setSessionId,
        messages,
        setMessages,
        sendMessage,
    };

    return <AiContext.Provider value={value}>{props.children}</AiContext.Provider>;
};
