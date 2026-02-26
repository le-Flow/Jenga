// @vitest-environment jsdom
import { cleanup, render, waitFor } from "@solidjs/testing-library";
import { afterEach, describe, expect, test, vi } from "vitest";
import { useContext } from "solid-js";
import { AuthContext, AuthProvider } from "./AuthProvider";
import { AuthenticationResourceService, OpenAPI } from "../api";

let authCtx: ReturnType<typeof useContext<typeof AuthContext>>;

const CaptureAuthContext = () => {
    authCtx = useContext(AuthContext);
    return null;
};

const renderAuthProvider = () =>
    render(() => (
        <AuthProvider>
            <CaptureAuthContext />
        </AuthProvider>
    ));

afterEach(() => {
    cleanup();
    vi.restoreAllMocks();
    OpenAPI.TOKEN = undefined;
    OpenAPI.USERNAME = undefined;
});

describe("AuthProvider", () => {
    test("login sets session values and logout clears them", async () => {
        vi.spyOn(AuthenticationResourceService, "postApiAuthLogin").mockResolvedValue({
            token: "token-123",
            username: "alice",
        } as never);

        renderAuthProvider();

        await waitFor(() => {
            expect(typeof authCtx?.login).toBe("function");
        });

        await authCtx?.login?.({ username: "alice", password: "secret" } as never);

        expect(authCtx?.isLoggedIn()).toBe(true);
        expect(authCtx?.jwt()?.username).toBe("alice");
        expect(OpenAPI.TOKEN).toBe("token-123");
        expect(OpenAPI.USERNAME).toBe("alice");

        authCtx?.logout?.();

        expect(authCtx?.isLoggedIn()).toBe(false);
        expect(authCtx?.jwt()).toBeUndefined();
        expect(OpenAPI.TOKEN).toBeUndefined();
        expect(OpenAPI.USERNAME).toBeUndefined();
    });

    test("login failure exposes loginError and keeps session empty", async () => {
        const authError = new Error("invalid credentials");
        vi.spyOn(AuthenticationResourceService, "postApiAuthLogin").mockRejectedValue(authError);

        renderAuthProvider();

        await waitFor(() => {
            expect(typeof authCtx?.login).toBe("function");
        });

        await authCtx?.login?.({ username: "alice", password: "wrong" } as never);

        expect(authCtx?.isLoggedIn()).toBe(false);
        expect(authCtx?.loginError()).toBe(authError);
        expect(OpenAPI.TOKEN).toBeUndefined();
        expect(OpenAPI.USERNAME).toBeUndefined();
    });
});
